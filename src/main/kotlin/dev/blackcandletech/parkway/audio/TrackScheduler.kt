package dev.blackcandletech.parkway.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.blackcandletech.parkway.Parkway
import net.dv8tion.jda.api.managers.AudioManager
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class TrackScheduler(val player: AudioPlayer): AudioEventAdapter() {

    val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()

    fun queue (track: AudioTrack) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track)
        }
    }

    fun nextTrack () {
        this.player.startTrack(this.queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack?, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext)
            nextTrack()
    }

    fun clearQueue() {
        queue.clear()
    }

    fun hasNext(): Boolean {
        return queue.isNotEmpty()
    }

}