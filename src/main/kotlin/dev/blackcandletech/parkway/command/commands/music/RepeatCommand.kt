package dev.blackcandletech.parkway.command.commands.music

import dev.blackcandletech.parkway.audio.RepeatingType
import dev.blackcandletech.parkway.command.SlashCommand
import dev.blackcandletech.parkway.guild.GuildManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class RepeatCommand: SlashCommand, ListenerAdapter() {

    private val words = arrayOf("Single", "Queue", "None")

    override fun getName(): String {
        return "repeat"
    }

    override fun getDescription(): String {
        return "Repeat the current song or queue!"
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isGuildOnly(): Boolean {
        return true
    }

    override fun getOptions(): MutableCollection<OptionData> {
        val options = mutableListOf<OptionData>()
        options.add(OptionData(OptionType.STRING, "type", "Single or Queue (none: toggle single/off)", false, true))
        return options
    }

    override fun execute(interaction: SlashCommandInteraction, args: Array<String>) {
        interaction.deferReply(false)
            .queue()
        val guild = interaction.guild!!
        val member = interaction.member!!
        if(!member.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            interaction.hook.editOriginal("You need the **`VOICE_MUTE_OTHERS`** permission in order to use this command!\nNote: We will be adding a DJ role soon.")
            return
        }

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
        val typeOption = interaction.getOption("type")
        if(typeOption == null) {
            val repeating = musicManager.scheduler.repeat
            if(repeating == RepeatingType.SINGLE || repeating == RepeatingType.QUEUE)
                musicManager.scheduler.repeat = RepeatingType.NONE
            else
                musicManager.scheduler.repeat = RepeatingType.SINGLE
        } else
            musicManager.scheduler.repeat = RepeatingType.valueOf(typeOption.asString.uppercase())

        val repeating = musicManager.scheduler.repeat
        val repeatingType = if (repeating == RepeatingType.NONE) {
            "no repeat"
        } else "repeat ${repeating.name.lowercase()}"
        interaction.hook.editOriginalFormat("The player has been set to **%s**!", repeatingType)
            .queue()
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        if(event.name == "repeat" && event.focusedOption.name == "type") {
            event.replyChoiceStrings(words.filter {
                it.startsWith(event.focusedOption.value)
            }).queue()
        }
    }

}