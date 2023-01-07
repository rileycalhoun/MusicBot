package dev.blackcandletech.parkway

import dev.blackcandletech.parkway.command.CommandManager
import dev.blackcandletech.parkway.data.JSONFile
import dev.blackcandletech.parkway.data.MongoConnector
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.EnumSet

class Parkway(token: String) {

    /*
    *   Static objects
    */
    companion object {

        private val logger: Logger = LoggerFactory.getLogger("Parkway")
        private val configJson: JSONFile = JSONFile("config.json", true)
        lateinit var mongo: MongoConnector
        lateinit var commandManager: CommandManager

        fun getLogger(): Logger {
            return logger
        }

        @JvmStatic
        fun main (args: Array<String>) {
            // If the config file does not exist, create it
            val token = configJson.getString("token")
            val mongoUri = configJson.getString("mongoUri")

//            mongo = MongoConnector(mongoUri)
            Parkway(token).run()
        }

        fun getConfig(): JSONFile {
            return configJson
        }

    }

    private val jdaBuilder: JDABuilder
    private lateinit var jda: JDA

    init {
        jdaBuilder = JDABuilder
            .createDefault(
                token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
            ).disableCache(EnumSet.of(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.ACTIVITY,
                CacheFlag.EMOJI,
                CacheFlag.STICKER,
                CacheFlag.SCHEDULED_EVENTS
            )).enableCache(CacheFlag.VOICE_STATE)
    }

    /*
    *   Run the Bot
    */
    fun run ()
    {
        jda = jdaBuilder
            .build ()
        Runtime.getRuntime().addShutdownHook(stop())
        commandManager = CommandManager(jda)
        registerEvents()
    }

    /*
    *   Shutdown the Bot
    */
    fun stop (): Thread
    {
        return Thread {

            run() {
                for(guild in jda.guildCache) {
                    if(guild.audioManager.isConnected)
                        guild.audioManager.closeAudioConnection()
                }

                jda.shutdown()
                logger.info("Shutting down!")
            }

        }
    }

    private fun registerEvents () {
        val clazzes = Reflections("dev.blackcandletech.parkway.events")
            .getSubTypesOf(ListenerAdapter::class.java)

        for (clazz in clazzes) {
            try {
                val event = clazz
                    .getDeclaredConstructor()
                    .newInstance()

                jda.addEventListener(event)
            } catch (_: Exception) {
                clazzes.remove(clazz)
            }
        }

        getLogger().info("Registered ${clazzes.size} events!")
    }

}