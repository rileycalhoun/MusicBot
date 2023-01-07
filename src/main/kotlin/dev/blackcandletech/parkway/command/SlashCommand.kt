package dev.blackcandletech.parkway.command

import net.dv8tion.jda.api.interactions.commands.Command.Option
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface SlashCommand {

    fun getName(): String
    fun getDescription(): String
    fun isRequired(): Boolean
    fun isGuildOnly(): Boolean
    fun execute(interaction: SlashCommandInteraction, args: Array<String>)

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