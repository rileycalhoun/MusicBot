package dev.blackcandletech.parkway.data

import dev.blackcandletech.parkway.Parkway
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

class JSONFile(private val fileName: String, private val copyFromResources: Boolean) {

    private val file: File = File(fileName)
    private val jsonObject: JsonObject

    init {
        if(!file.exists()) createFile()
        val jsonContent = getFileAsString()
        jsonObject = Json.parseToJsonElement(jsonContent).jsonObject
    }

    private fun createFile () {
        // Create a blank config file and create an output stream
        file.createNewFile();
        if(copyFromResources) {
            val fileStream = FileOutputStream(file)

            // If the resource can be found in the resources, then write the template data to blank config file
            this.javaClass.classLoader.getResourceAsStream(fileName)?.transferTo(fileStream)
        }
    }

    private fun getFileAsString(): String {
        val reader = BufferedReader(FileReader(file))
        val stringBuilder = StringBuffer()
        var line = reader.readLine()
        val newLine = System.getProperty("line.separator")

        while(line != null) {
            stringBuilder.append(line)
            stringBuilder.append(newLine)
            line = reader.readLine()
        }

        return stringBuilder.toString()
    }

    fun getMap(): JsonObject {
        return jsonObject
    }

    fun getValue(key: String): JsonElement? {
        return jsonObject[key]
    }

    fun getString(key: String): String {
        return getValue(key)!!.jsonPrimitive.content
    }


}