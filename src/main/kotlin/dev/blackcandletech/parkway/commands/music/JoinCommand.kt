package dev.blackcandletech.parkway.commands.music

import dev.blackcandletech.parkway.api.audio.ExecutorChannelState
import dev.blackcandletech.parkway.api.command.CommandContext
import dev.blackcandletech.parkway.api.command.SlashCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class JoinCommand: SlashCommand {

    override fun getName(): String {
        return "join"
    }

    override fun getDescription(): String {
        return "Join the channel you are currently in!"
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isGuildOnly(): Boolean {
        return true
    }

    override fun getOptions(): MutableCollection<OptionData> {
        val options = mutableListOf<OptionData>()
        options.add(
            OptionData(
                OptionType.BOOLEAN,
                "force",
                "Force the bot to leave another voice channel to join your own."
            )
        )
        return options
    }

    override fun execute(context: CommandContext) {
        val interaction = context.getInteraction()
        interaction.deferReply(true)
            .queue()
        val member = context.getExecutorAsMember()!!
        val force = (interaction.getOption("force")?.asBoolean == true) && (member.hasPermission(Permission.VOICE_MOVE_OTHERS))
        val state = context.joinExecutorAudioChannel(force)

        val message = StringBuilder()
        when(state) {
            ExecutorChannelState.SUCCESS -> message.append("Connecting to **`\uD83D\uDD0A %s`**!", context.getSelfVoiceState()!!.channel!!.name)
            ExecutorChannelState.NO_PERMISSION -> message.append("I don't have permission to join %s!", context.getExecutorVoiceState()!!.channel!!.name)
            ExecutorChannelState.NOT_IN_VOICE -> message.append("You're not currently in a voice channel!")
            ExecutorChannelState.NOT_IN_SAME_VOICE -> message.append("I'm already in a voice channel!")
            ExecutorChannelState.IN_SAME_VOICE -> message.append("We're already in the same voice channel!")
        }

        interaction.hook.editOriginal(message.toString())
            .queue()
        return
    }

}