package dev.blackcandletech.parkway.api.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction


data class CommandContext(private val interaction: SlashCommandInteraction, private val args: Array<String>) {

    private val selfMember: Member? = interaction.guild?.selfMember
    private val guild: Guild? = interaction.guild
    private val executorAsMember: Member? = interaction.member
    private val executorAsUser: User = interaction.user

    fun getInteraction (): SlashCommandInteraction {
        return interaction
    }

    fun getGuild (): Guild? {
        return guild
    }

    fun getSelfMember (): Member? {
        return selfMember
    }

    fun getSelfVoiceState(): GuildVoiceState? {
        return selfMember?.voiceState
    }

    /**
     * @apiNote Guild only
     */
    fun selfHasPermission(permission: Permission): Boolean {
        return getSelfMember()!!.hasPermission(permission)
    }

    /**
     * @apiNote Guild only
     */
    fun selfHasPermission(channel: GuildChannel, permission: Permission): Boolean {
        return getSelfMember()!!.hasPermission(channel, permission)
    }

    fun getExecutorAsMember (): Member? {
        return executorAsMember
    }

    fun getExecutorAsUser (): User {
        return executorAsUser
    }

    fun getExecutorVoiceState(): GuildVoiceState? {
        return executorAsMember?.voiceState
    }

    /**
     * @apiNote Guild only
     */
    fun executorHasPermission(permission: Permission): Boolean {
        return getExecutorAsMember()!!.hasPermission(permission)
    }

    /**
     * @apiNote Guild only
     */
    fun executorHasPermission(channel: GuildChannel, permission: Permission): Boolean {
        return getExecutorAsMember()!!.hasPermission(channel, permission)
    }

    /**
     * @apiNote Guild only
     */
    fun isExecutorInAudioChannel(): Boolean {
        return getExecutorAsMember()?.voiceState!!.inAudioChannel()
    }

    /**
     * @apiNote Guild only
     */
    fun joinExecutorAudioChannel (force: Boolean): String {
        if(getExecutorVoiceState()!!.inAudioChannel())
            return "You are not in a voice channel!"
        if(getSelfVoiceState()!!.inAudioChannel() && !force)
            return "I'm already in a voice channel!"

        val audioManager = getGuild()!!.audioManager
        val channel = getExecutorVoiceState()!!.channel!!

        if(!selfHasPermission(channel, Permission.VOICE_CONNECT))
            return String.format("I don't have permission to join %s!", getSelfVoiceState()!!.channel!!.name)

        audioManager.openAudioConnection(channel)
        return String.format("Connecting to **`\uD83D\uDD0A %s`**!", getSelfVoiceState()!!.channel!!.name)
    }

    fun inSameAudioChannel (): String? {
        if(getSelfVoiceState()!!.inAudioChannel())
            return "I need to be in a voice channel!"
        if(getExecutorVoiceState()!!.inAudioChannel())
            return "You're not in a voice channel!"
        if(getExecutorVoiceState()!!.channel != getSelfVoiceState()!!.channel)
            return "You need to be in thes same voice channel as me!"

        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandContext

        if (interaction != other.interaction) return false
        if (!args.contentEquals(other.args)) return false
        if (selfMember != other.selfMember) return false
        if (executorAsMember != other.executorAsMember) return false
        if (executorAsUser != other.executorAsUser) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interaction.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + (selfMember?.hashCode() ?: 0)
        result = 31 * result + (executorAsMember?.hashCode() ?: 0)
        result = 31 * result + executorAsUser.hashCode()
        return result
    }

    override fun toString(): String {
        return interaction.fullCommandName
    }

}