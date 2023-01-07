package dev.blackcandletech.parkway.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

class GuildMusicManager(private val audioPlayerManager: AudioPlayerManager) {

    val audioPlayer: AudioPlayer = audioPlayerManager.createPlayer()
    val scheduler: TrackScheduler = TrackScheduler(this.audioPlayer)
    private val sendHandler = AudioPlayerSendHandler(this.audioPlayer)

    init {
        this.audioPlayer.addListener(this.scheduler)
    }

    fun getSendHandler(): AudioPlayerSendHandler {
        return sendHandler
    }

    fun joinVoiceChannel(interaction: SlashCommandInteraction, force: Boolean, reply: Boolean): Boolean {
        val member = interaction.member!!
        val guild = interaction.guild!!
        val self = guild.selfMember

        if(self.voiceState!!.inAudioChannel() && !force) {
            if(reply) interaction.hook.editOriginal("I'm already in a voice channel!")
                .queue()
            return false
        }

        if(!(member.voiceState!!.inAudioChannel())) {
            if(reply) interaction.hook.editOriginal("You're not in a voice channel!")
                .queue()
            return false
        }

        val audioManager = guild.audioManager
        val voiceChannel = member.voiceState!!.channel!!

        if(!(self.hasPermission(voiceChannel, Permission.VOICE_CONNECT))) {
            if(reply) interaction.hook.editOriginalFormat("I don't have permission to join %s!", voiceChannel.name)
            return false
        }

        audioManager.openAudioConnection(voiceChannel)
        if(reply) interaction.hook.editOriginalFormat("Connecting to **`\uD83D\uDD0A %s`**!", voiceChannel.name)
            .queue()
        return true
    }

    fun loadAndPlay(interaction: SlashCommandInteraction, trackURL: String) {
        audioPlayerManager.loadItemOrdered(this, trackURL, object: AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                scheduler.queue(track)
                interaction.hook.editOriginal("Added **`${track.info.title}`** by **`${track.info.author}`** to the queue!")
                    .queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if(playlist.isSearchResult) {
                    trackLoaded(playlist.tracks[0])
                    return
                }

                for(track in playlist.tracks)
                    scheduler.queue(track)

                interaction.hook.editOriginal("Added **`${playlist.tracks.size} songs`** to the queue!")
                    .queue()
            }

            override fun noMatches() {
                interaction.hook.editOriginal("No matches found for **`${trackURL}`**")
                    .queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                if (exception.severity == FriendlyException.Severity.COMMON) {
                    interaction.hook.editOriginal("There was an error with YouTube while trying to play that video! (It could be blocked in my region!)")
                    return
                } else interaction.hook.editOriginal("There was an error while trying to load a track!")
            }

        })
    }

}