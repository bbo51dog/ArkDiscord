package net.bbo51dog.arkdiscord

import net.bbo51dog.arkdiscord.ark.ArkRconClient
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Instant


class EventListener(
    private val rcon: ArkRconClient
) : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        println("Bot has started.")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "status" -> {
                val eb = EmbedBuilder()
                eb.setAuthor("\uD83E\uDD96BboArk\uD83E\uDD95")
                    .setDescription("Status of BboArk server.")

                val players = rcon.getOnlinePlayers()
                if (rcon.isOnline()) {
                    eb.setTitle("✅Server Online")
                        .setColor(Color.GREEN)
                        .addField("${players!!.size} Players", players.joinToString(", "), false)
                        .setTimestamp(Instant.now())
                } else {
                    eb.setTitle("❌Server Offline")
                        .setColor(Color.RED)
                        .setTimestamp(Instant.now())
                }
                event.replyEmbeds(eb.build()).setEphemeral(false).queue()
            }
            else -> event.reply("Unknown command").setEphemeral(false).queue()
        }
    }
}