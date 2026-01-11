package net.bbo51dog.arkdiscord

import net.bbo51dog.arkdiscord.ark.ArkRconClient
import net.bbo51dog.arkdiscord.utils.Config
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.system.exitProcess


const val CONFIG_FILE_NAME = "config.properties"

fun main() {
    ensureConfigFile()
    val config = loadConfig()

    val arkClient = ArkRconClient.connect(
        config.host,
        config.port,
        config.password,
        config.steamKey
    )
    val jda = JDABuilder.createLight(
        config.token,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT,
    )
        .addEventListeners(EventListener(arkClient))
        .setActivity(Activity.playing("BboArk"))
        .build()
    jda.awaitReady()
    jda.updateCommands().queue()
    jda.upsertCommand("status","サーバーステータスを表示").queue()
    val guild = jda.getGuildById(1045877992368394240)
    //guild!!.upsertCommand("status","サーバーステータスを表示").queue()
}

fun ensureConfigFile() {
    val target = File(CONFIG_FILE_NAME)
    if (target.exists()) return

    val input = object {}.javaClass.classLoader.getResourceAsStream(CONFIG_FILE_NAME)
    input.use {
        Files.copy(it, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    println("$CONFIG_FILE_NAME not found.\nPlease edit it.")
    exitProcess(0)
}

fun loadConfig(): Config {
    val properties =  Properties().apply { File(CONFIG_FILE_NAME).inputStream().use(this::load) }
    return Config.load(properties)
}

