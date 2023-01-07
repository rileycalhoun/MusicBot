package dev.blackcandletech.parkway.command.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

class QueueCommand: SlashCommand {

    override fun getName(): String {
        return "queue"
    }

    override fun getDescription(): String {
        return "Get the current queue of songs!"
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
        val musicManager = GuildManager.getInstance().getMusicManager(guild)
        val queue = musicManager.scheduler.queue

        if(queue.isEmpty()) {
            interaction.hook.editOriginal("There are no songs in the queue!")
                .queue()
            return
        }

        val trackCount = queue.size.coerceAtMost(20)
        val trackList: List<AudioTrack> = ArrayList(queue)
        val messageBuilder = StringBuilder("**Current Queue:**\n")

        var i = 0
        while(i < trackCount) {
            val track = trackList[i]
            messageBuilder.append("${i + 1}. **`${track.info.title}`** by **`${track.info.author}`**")
            if((i + 1) != trackCount) messageBuilder.append("\n")
            i++
        }

        interaction.hook.editOriginal(messageBuilder.toString())
            .queue()
    }

}