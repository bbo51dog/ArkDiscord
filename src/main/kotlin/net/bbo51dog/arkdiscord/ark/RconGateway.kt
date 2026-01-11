package net.bbo51dog.arkdiscord.ark

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class RconGateway(
    private val host: String,
    private val port: Int,
    private val password: String,
) {

    private var socket: Socket? = null
    private var input: DataInputStream? = null
    private var output: DataOutputStream? = null
    private var reqId = 1

    private val charset = Charset.forName("ISO-8859-1")

    //fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false
    fun isConnected(): Boolean {
        val s = socket ?: return false
        return s.isConnected && !s.isClosed && !s.isInputShutdown && !s.isOutputShutdown
    }

    fun connect() {
        close()
        reqId = 1

        val s = Socket(host, port)
        s.soTimeout = 5000
        input = DataInputStream(s.getInputStream())
        output = DataOutputStream(s.getOutputStream())

        socket = s

        send(reqId, 3, password) // AUTH
        /*
        read()                  // empty
        val resp = read()
        if (resp.id == -1) {
            close()
            error("RCON auth failed")
        }

         */

        val p1 = read()
        if (p1.type == 2) {
            // これがAUTH_RESPONSE
            if (p1.id == -1) error("RCON auth failed")
            return
        }

        // そうでなければ empty → 次がAUTH
        val p2 = read()
        if (p2.id == -1) error("RCON auth failed")
    }

    fun close() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        input = null
        output = null
    }

    fun exec(cmd: RconCommand, retry: Int = 3, interval: Long = 1000): String {
        var lastError: Exception? = null

        repeat(retry) { attempt ->
            try {
                if (!isConnected()) {
                    connect()
                }

                val res = execCommand(cmd.label)

                // ARKハング検知
                if (res.contains("Server received, But no response!!")) {
                    throw RuntimeException("ARK RCON hung")
                }

                return res
            } catch (e: Exception) {
                lastError = e
                connect()

                if (attempt < retry - 1) {
                    Thread.sleep(interval)
                }
            }
        }
        throw RuntimeException("RCON failed after $retry retries", lastError)
    }

    private fun execCommand(cmd: String): String {
        if (!isConnected()) error("RCON not connected")

        val id = ++reqId
        send(id, 2, cmd)
        send(id + 1, 2, "")   // end marker

        val sb = StringBuilder()
        while (true) {
            val p = read()
            if (p.id == id + 1) break
            sb.append(p.body)
        }
        return sb.toString()
    }

    private fun send(id: Int, type: Int, body: String) {
        val data = body.toByteArray(charset)
        val buf = ByteBuffer.allocate(4 + 4 + 4 + data.size + 2).order(ByteOrder.LITTLE_ENDIAN)
        buf.putInt(4 + 4 + data.size + 2)
        buf.putInt(id)
        buf.putInt(type)
        buf.put(data)
        buf.put(0).put(0)
        output!!.write(buf.array())
        output!!.flush()
    }

    private fun read(): RconPacket {
        try {
            val len = Integer.reverseBytes(input!!.readInt())
            val data = input!!.readNBytes(len)
            val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            val id = buf.int
            val type = buf.int
            val str = ByteArray(len - 8 - 2)
            buf.get(str)
            return RconPacket(id, type, String(str, charset))
        } catch (e: Exception) {
            close()
            throw e
        }
    }
}