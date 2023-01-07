package dev.blackcandletech.parkway.data

import com.mongodb.client.MongoCollection
import dev.blackcandletech.parkway.data.struct.GuildData
import org.litote.kmongo.*

class MongoConnector(mongoUri: String) {

    private val client = KMongo.createClient(mongoUri)
    private val database = client.getDatabase("parkway")

    fun getGuilds(): MongoCollection<GuildData> {
        return database.getCollection()
    }

}