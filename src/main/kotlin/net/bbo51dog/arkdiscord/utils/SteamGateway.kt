package net.bbo51dog.arkdiscord.utils

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI

class SteamGateway(
    private val key: String
) {

    private val playerNameCache = mutableMapOf<Long, String>()

    fun getPlayerName(id: Long): String {
        if (playerNameCache.containsKey(id)) return playerNameCache[id]!!
        val url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=$key&steamids=$id"
        val con: HttpURLConnection = URI(url).toURL().openConnection() as HttpURLConnection
        con.setRequestProperty("Accept-Charset", "UTF-8")

        val str = con.inputStream.bufferedReader(Charsets.UTF_8).use { br ->
            br.readLines().joinToString("")
        }
        val json = JSONObject(str)
        val name = json.getJSONObject("response")
            .getJSONArray("players")
            .getJSONObject(0)
            .getString("personaname")
        playerNameCache[id] = name
        return name
    }
}