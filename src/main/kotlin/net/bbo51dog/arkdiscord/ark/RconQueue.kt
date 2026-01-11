package net.bbo51dog.arkdiscord.ark

import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread


class RconQueue(private val rcon: RconGateway) {

    private data class Task(
        val cmd: RconCommand,
        val future: java.util.concurrent.CompletableFuture<String>
    )

    private val queue = LinkedBlockingQueue<Task>()
    @Volatile private var running = true

    init {
        thread(isDaemon = true, name = "ARK-RCON-Queue") {
            worker()
        }
    }

    private fun worker() {
        while (running) {
            try {
                val task = queue.take()
                try {
                    val result = rcon.exec(task.cmd)
                    task.future.complete(result)
                } catch (e: Exception) {
                    task.future.completeExceptionally(e)
                }
                Thread.sleep(1000)
            } catch (_: InterruptedException) {
            }
        }
    }

    fun exec(cmd: RconCommand): String {
        val f = java.util.concurrent.CompletableFuture<String>()
        queue.put(Task(cmd, f))
        return f.get()
    }

    fun isConnected() = rcon.isConnected()

    fun stop() {
        running = false
    }
}