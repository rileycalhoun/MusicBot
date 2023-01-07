package dev.blackcandletech.parkway.events

import dev.blackcandletech.parkway.Parkway
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandListener: ListenerAdapter() {

    private val commandManager = Parkway.commandManager

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        val interaction = event.interaction
        commandManager.execute(interaction)

    }

}