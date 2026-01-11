package net.bbo51dog.arkdiscord.ark

import net.bbo51dog.arkdiscord.utils.SteamGateway

class ArkRconClient private constructor(
    private val rcon: RconQueue,
    private val steam: SteamGateway
) {

    companion object{
        fun connect(host: String, port: Int, password: String, steamKey: String): ArkRconClient {
            val rconGateway = RconGateway(host, port, password)
            rconGateway.connect()
            val queue = RconQueue(rconGateway)
            val steamGateway = SteamGateway(steamKey)
            return ArkRconClient(queue, steamGateway)
        }
    }

    fun isOnline(): Boolean {
        return rcon.isConnected()
    }

    fun getOnlinePlayers(): List<String>? {
        try {
            val response = rcon.exec(RconCommand.LIST_PLAYERS)
            val names = mutableListOf<String>()

            for (line in response.lines()) {
                val m = Regex("""\d+\.\s*.*?,\s*(\d{17})""").find(line) ?: continue
                val id = m.groupValues[1].toLong()
                names += steam.getPlayerName(id)
            }
            return names
        } catch (e: Exception) {
            return null
        }
    }

    fun getMap(): String? {
        return try {
            rcon.exec(RconCommand.GET_MAP).trim()
        } catch (e: Exception) {
            null
        }
    }

    fun getVersion(): String? {
        return try {
             rcon.exec(RconCommand.GET_VERSION).trim()
        } catch (e: Exception) {
            null
        }
    }

    fun getDay(): Int? {
        return try {
            rcon.exec(RconCommand.GET_DAY).trim().toInt()
        } catch (e: Exception) {
            null
        }
    }
}