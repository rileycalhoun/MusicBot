package dev.blackcandletech.parkway.commands.music

import dev.blackcandletech.parkway.api.command.CommandContext
import dev.blackcandletech.parkway.api.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager

class NowPlayingCommand: SlashCommand {
    override fun getName(): String {
        return "nowplaying"
    }

    override fun getDescription(): String {
        return "See the track that is currently playing!"
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
        val guild = context.getGuild()!!
        val musicManager = GuildManager.getInstance().getMusicManager(guild)

        val audioPlayer = musicManager.audioPlayer
        if(audioPlayer.playingTrack == null) {
            interaction.hook.editOriginal("There is no music playing currently!")
            return
        }

        val track = musicManager.audioPlayer.playingTrack
        interaction.hook.editOriginal("I'm currently playing **`${track.info.title}`** by **`${track.info.author}`**")
            .queue()
    }
}