package net.bbo51dog.arkdiscord.utils

import java.util.Properties

class Config(
    val host: String,
    val port: Int,
    val password: String,
    val steamKey: String,
    val token: String,
) {

    companion object {
        fun load(p: Properties): Config {
            return Config(
                p.getProperty("rcon_host"),
                p.getProperty("rcon_port").toInt(),
                p.getProperty("rcon_password"),
                p.getProperty("steam_apikey"),
                p.getProperty("discord_token"),
                )
        }
    }
}