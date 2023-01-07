package dev.blackcandletech.parkway.events

import dev.blackcandletech.parkway.Parkway
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.Presence
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ReadyListener: ListenerAdapter() {

    private val logger = Parkway.getLogger()
    private val presences = mutableMapOf<Activity, OnlineStatus>()

    init {
        presences[Activity.watching("your mom!")] = OnlineStatus.DO_NOT_DISTURB
        presences[Activity.playing("Minecraft.")] = OnlineStatus.DO_NOT_DISTURB
    }

    override fun onReady(event: ReadyEvent) {
        val tag = event.jda.selfUser.asTag
        logger.info("Ready to receive command on $tag")

        val executorService = Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleAtFixedRate(setPresence(event.jda.presence), 0, 15, TimeUnit.SECONDS)
    }

    private fun setPresence (presence: Presence): Thread {
        return Thread {
            run () {
                val num = Random().nextInt(presences.size)

                val activity = presences.keys.toTypedArray()[num]
                val onlineStatus = presences[activity]

                presence.setPresence(onlineStatus, activity)
            }
        }
    }

}