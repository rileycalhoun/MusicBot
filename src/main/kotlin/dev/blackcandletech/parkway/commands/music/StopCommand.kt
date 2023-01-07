package dev.blackcandletech.parkway.commands.music

import dev.blackcandletech.parkway.api.command.CommandContext
import dev.blackcandletech.parkway.api.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.Permission

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

    override fun execute(context: CommandContext) {
        val interaction = context.getInteraction()
        interaction.deferReply(false)
            .queue()
        val guild = interaction.guild!!
        val member = interaction.member!!
        if(!member.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            interaction.hook.editOriginal("You need the **`VOICE_MUTE_OTHERS`** permission in order to use this command!\nNote: We will be adding a DJ role soon.")
            return
        }

        val response = context.inSameAudioChannel()
        if(response != null) {
            interaction.hook.editOriginal(response)
                .queue()
            return
        }

        val musicManager = GuildManager.getInstance().getMusicManager(guild)
        musicManager.scheduler.player.stopTrack()
        musicManager.scheduler.clearQueue()
        guild.audioManager.closeAudioConnection()

        interaction.hook.editOriginal("The player has been stopped!")
            .queue()
    }

}