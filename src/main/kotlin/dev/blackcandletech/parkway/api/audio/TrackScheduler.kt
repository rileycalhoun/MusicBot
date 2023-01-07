package dev.blackcandletech.parkway.api.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class TrackScheduler(val player: AudioPlayer): AudioEventAdapter() {

    val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
    var repeat: RepeatingType = RepeatingType.NONE

    fun queue (track: AudioTrack) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track)
        }
    }

    fun nextTrack () {
        this.player.startTrack(this.queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            if(this.repeat == RepeatingType.SINGLE) {
                this.player.startTrack(track.makeClone(), false)
                return
            }

            nextTrack()
            if(this.repeat == RepeatingType.QUEUE)
                queue(track.makeClone())
        }
    }

    fun clearQueue() {
        queue.clear()
    }

    fun hasNext(): Boolean {
        return queue.isNotEmpty()
    }

}