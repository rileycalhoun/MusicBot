package dev.blackcandletech.parkway.api.command

import dev.blackcandletech.parkway.api.audio.ExecutorChannelState
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface SlashCommand {

    fun getName(): String
    fun getDescription(): String
    fun isRequired(): Boolean
    fun isGuildOnly(): Boolean
    fun execute(context: CommandContext)

    fun getSubCommands(): MutableCollection<SubcommandData>? {
        return null
    }

    fun getOptions(): MutableCollection<OptionData>? {
        return null
    }

    fun getAliases(): Array<String>? {
        return null
    }

}