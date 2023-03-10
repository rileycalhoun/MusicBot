package dev.blackcandletech.parkway.api.command

import dev.blackcandletech.parkway.Parkway
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import org.reflections.Reflections

class CommandManager(val jda: JDA) {

    val logger = Parkway.getLogger()
    private val commands: MutableList<SlashCommand> = mutableListOf()

    init {
        findCommands()
        mapAllGuildCommands()
    }

    fun execute(interaction: SlashCommandInteraction) {
        val commandString = interaction.fullCommandName.split(" ").toTypedArray()
        val commandName = commandString[0]
        val args = commandString.copyOfRange(1, commandString.size)

        val command = commands.find { it.getName() == commandName.lowercase() } ?: return
        val context = CommandContext(interaction, args)
        command.execute(context)
    }

    private fun findCommands () {
        val clazzes = Reflections("dev.blackcandletech.parkway.commands")
            .getSubTypesOf(SlashCommand::class.java)

        for (clazz in clazzes) {
            val command = clazz
                .getDeclaredConstructor()
                .newInstance()

            commands.add(command)
        }
    }

    private fun mapAllGuildCommands () {
        // Map the required commands globally
        for(command in commands) {
            if(command.isRequired()) {
                val data = CommandDataImpl(command.getName(), command.getDescription())
                if(command.getSubCommands() != null)
                    data.addSubcommands(command.getSubCommands()!!)
                else if(command.getOptions() != null) {
                    var autoComplete = false
                    for(option in command.getOptions()!!) {
                        val instanceOfAutocomplete = command is ListenerAdapter
                        if(option.isAutoComplete && (instanceOfAutocomplete))
                            autoComplete = true
                        data.addOptions(option)
                    }

                    if(autoComplete) jda.addEventListener(command)
                }

                jda.upsertCommand(data).queue()
            }
        }

        for(guild in jda.guildCache)
            mapGuildCommands(guild)
    }

    fun mapGuildCommands (guild: Guild) {
        // TODO: Map any commands added by the guild
    }

}