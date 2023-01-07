package dev.blackcandletech.parkway.guild

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.blackcandletech.parkway.api.audio.GuildMusicManager
import dev.blackcandletech.parkway.data.struct.GuildData
import net.dv8tion.jda.api.entities.Guild

class GuildManager {

    companion object {
        private var INSTANCE: GuildManager = GuildManager()
        fun getInstance(): GuildManager {
            return INSTANCE
        }
    }

    private val guildData: Map<Guild, GuildData> = HashMap()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = HashMap()
    private val audioPlayerManager: AudioPlayerManager = DefaultAudioPlayerManager()

    init {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager)
    }

    fun getMusicManager(guild: Guild): GuildMusicManager {
        return this.musicManagers.getOrElse(guild.idLong) {
            val musicManager = GuildMusicManager(this.audioPlayerManager)
            guild.audioManager.sendingHandler = musicManager.getSendHandler()
            musicManagers[guild.idLong] = musicManager
            return musicManager
        }
    }

}