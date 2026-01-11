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
        if (!isOnline()) return null
        val response = rcon.exec(RconCommand.LIST_PLAYERS)
        val names = mutableListOf<String>()

        for (line in response.lines()) {
            val m = Regex("""\d+\.\s*.*?,\s*(\d{17})""").find(line) ?: continue
            val id = m.groupValues[1].toLong()
            names += steam.getPlayerName(id)
        }
        return names
    }

    fun getMap(): String? {
        if (!isOnline()) return null
        return rcon.exec(RconCommand.GET_MAP).trim()
    }

    fun getVersion(): String? {
        if (!isOnline()) return null
        return rcon.exec(RconCommand.GET_VERSION).trim()
    }

    fun getDay(): Int? {
        if (!isOnline()) return null
        return rcon.exec(RconCommand.GET_DAY).trim().toInt()
    }
}