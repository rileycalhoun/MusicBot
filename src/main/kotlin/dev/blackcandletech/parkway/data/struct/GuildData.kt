package dev.blackcandletech.parkway.data.struct

import dev.blackcandletech.parkway.command.SlashCommand

data class GuildData(
    val name: String,
    val id: String,
    val prefix: String,
    val enabledCommands: ArrayList<SlashCommand>
)
