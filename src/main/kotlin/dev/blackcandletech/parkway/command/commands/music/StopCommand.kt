package dev.blackcandletech.parkway.command.commands.music

import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

class StopCommand: SlashCommand {

    override fun getName(): String {
        return "stop"
    }

    override fun getDescription(): String {
        return "Stop the current playing song!"
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isGuildOnly(): Boolean {
        return true
    }

    override fun execute(interaction: SlashCommandInteraction, args: Array<String>) {
        interaction.deferReply(false)
            .queue()
        val guild = interaction.guild!!
        val member = interaction.member!!
        if(!member.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            interaction.hook.editOriginal("You need the **`VOICE_MUTE_OTHERS`** permission in order to use this command!\nNote: We will be adding a DJ role soon.")
            return
        }

        val self = guild.selfMember
        val musicManager = GuildManager.getInstance().getMusicManager(guild)
        if(!musicManager.inSameVoiceChannel(self, member, interaction, true))
            return

        musicManager.scheduler.player.stopTrack()
        musicManager.scheduler.clearQueue()
        guild.audioManager.closeAudioConnection()

        interaction.hook.editOriginal("The player has been stopped!")
            .queue()
    }

}