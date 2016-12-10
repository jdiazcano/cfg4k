package com.jdiazcano.konfig.loaders

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.binding.prefix
import java.net.URL

open class JsonConfigLoader(
        protected val url: URL
): ConfigLoader {

    protected val parser = Parser()
    protected val properties = mutableMapOf<String, String>()

    init {
        loadProperties()
    }

    override fun reload() {
        loadProperties()
    }

    protected fun loadProperties() {
        val stream = url.openStream()
        val json = parser.parse(stream) as JsonObject
        properties.clear()
        properties.putAll(reduce(json))
        stream.close()
    }

    override fun get(key: String) = properties[key]?: ""

    private fun reduce(json: JsonObject, prefix: String = ""): MutableMap<String, String> {
        val properties = mutableMapOf<String, String>()
        reduceInternal(properties, json, prefix)
        return properties
    }

    private fun reduceInternal(properties: MutableMap<String, String>, json: JsonObject, prefix: String = "") {
        json.forEach { key, value ->
            when (value) {
                is JsonObject -> reduceInternal(properties, value, prefix(prefix, key))
                else -> properties[prefix(prefix, key)] = value.toString()
            }
        }
    }
}
