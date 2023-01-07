package dev.blackcandletech.parkway.commands.music

import dev.blackcandletech.parkway.api.command.CommandContext
import dev.blackcandletech.parkway.api.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.net.URI
import java.net.URISyntaxException

class PlayCommand: SlashCommand {

    override fun getName(): String {
        return "play"
    }

    override fun getDescription(): String {
        return "Play a song!"
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isGuildOnly(): Boolean {
        return true
    }

    override fun getOptions(): MutableCollection<OptionData>? {
        val options = mutableListOf<OptionData>()
        options.add(OptionData(OptionType.STRING, "song", "The song you want to play!", true, false))
        return options
    }

    override fun execute(context: CommandContext) {
        val interaction = context.getInteraction()
        interaction.deferReply(false)
            .queue()
        if(!context.getSelfVoiceState()!!.inAudioChannel())
            context.joinExecutorAudioChannel(false)

        val guild = context.getGuild()!!
        var song = interaction.getOption("song")!!.asString
        if(!isURL(song))
            song = "ytsearch:$song audio"
        GuildManager.getInstance().getMusicManager(guild).loadAndPlay(interaction, song)
    }

    private fun isURL(song: String): Boolean {
        return try {
            URI(song)
            true
        } catch (_: URISyntaxException) {
            false
        }
    }

}