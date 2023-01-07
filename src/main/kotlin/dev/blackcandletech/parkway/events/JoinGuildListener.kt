package dev.blackcandletech.parkway.events

import dev.blackcandletech.parkway.Parkway
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class JoinGuildListener: ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        Parkway.commandManager.mapGuildCommands(event.guild)
    }

}