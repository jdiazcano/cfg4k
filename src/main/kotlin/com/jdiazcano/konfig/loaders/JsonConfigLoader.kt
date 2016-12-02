package com.jdiazcano.konfig.loaders

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.binding.prefix
import java.io.InputStream

class JsonConfigLoader(
        inputStream: InputStream
): ConfigLoader {
    val parser = Parser()
    val properties = mutableMapOf<String, String>()

    init {
        val json = parser.parse(inputStream) as JsonObject
        reduce(json)
    }

    override fun get(key: String) = properties[key]?: ""

    fun reduce(json: JsonObject, prefix: String = "") {
        json.forEach { key, value ->
            when (value) {
                is JsonObject -> reduce(value, prefix(prefix, key))
                else -> properties[prefix(prefix, key)] = value.toString()
            }
        }
    }
}
