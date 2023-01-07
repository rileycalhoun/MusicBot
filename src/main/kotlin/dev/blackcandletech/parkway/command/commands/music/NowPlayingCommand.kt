package dev.blackcandletech.parkway.command.commands.music

import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

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

    override fun execute(interaction: SlashCommandInteraction, args: Array<String>) {
        interaction.deferReply(false)
            .queue()
        val guild = interaction.guild!!
        val member = interaction.member!!
        val self = guild.selfMember
        val selfVoicestate = self.voiceState!!

        if(!selfVoicestate.inAudioChannel()) {
            interaction.hook.editOriginal("I'm not currently in a voice channel!")
                .queue()
            return
        }

        val memberVoiceState = member.voiceState!!

        if(!memberVoiceState.inAudioChannel()) {
            interaction.hook.editOriginal("You need to be in the same voice channel as me in order to use this command!")
                .queue()
            return
        }

        if(memberVoiceState.channel != selfVoicestate.channel) {
            interaction.hook.editOriginal("You need to be in the same voice channel as me in order to use this command!")
                .queue()
            return
        }

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