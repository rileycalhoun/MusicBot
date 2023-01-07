package dev.blackcandletech.parkway.commands.music

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
        val response = context.joinExecutorAudioChannel(force)
        interaction.hook.editOriginal(response)
            .queue()
        return
    }

}