package dev.blackcandletech.parkway.command.commands.music

import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class JoinCommand: SlashCommand {

    override fun getName(): String {
        return "join"
    }

    override fun getDescription(): String {
        return "Join the channel you are currently in!"
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isGuildOnly(): Boolean {
        return true
    }

    override fun getOptions(): MutableCollection<OptionData>? {
        val options = mutableListOf<OptionData>()
        options.add(OptionData(OptionType.BOOLEAN, "force", "Force the bot to leave another voice channel to join your own."))
        return options
    }

    override fun execute(interaction: SlashCommandInteraction, args: Array<String>) {
        interaction.deferReply(true)
            .queue()
        val guild = interaction.guild!!
        val self = guild.selfMember
        val member = interaction.member!!
        val force = (interaction.getOption("force")?.asBoolean == true) && (member.hasPermission(Permission.VOICE_MOVE_OTHERS))
        GuildManager.getInstance().getMusicManager(guild).joinVoiceChannel(self, member, interaction, force, true)
        return
    }

}