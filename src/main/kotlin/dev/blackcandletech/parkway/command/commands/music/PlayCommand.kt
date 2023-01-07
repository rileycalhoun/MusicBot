package dev.blackcandletech.parkway.command.commands.music

import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
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

    override fun execute(interaction: SlashCommandInteraction, args: Array<String>) {
        interaction.deferReply(false).queue()

        val guild = interaction.guild!!
        val member = interaction.member!!
        val self = interaction.guild!!.selfMember
        val selfVoiceState = self.voiceState!!

        if(!selfVoiceState.inAudioChannel()) {
            val joined = GuildManager.getInstance().getMusicManager(guild).joinVoiceChannel(self, member, interaction, force = false, reply = false)
            if(!joined) {
                interaction.hook
                    .editOriginalFormat("There was an error while attempting to connect to the voice channel. Make sure you're in a voice channel that I have permission to access!")
                    .queue()
                return
            }
        }

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