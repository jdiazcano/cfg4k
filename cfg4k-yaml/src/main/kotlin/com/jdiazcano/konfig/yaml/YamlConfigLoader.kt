package com.jdiazcano.konfig.yaml

import com.jdiazcano.konfig.loaders.ConfigLoader
import org.yaml.snakeyaml.Yaml
import com.jdiazcano.konfig.binding.prefix
import java.net.URL

class YamlConfigLoader(private val url: URL): ConfigLoader {
    val properties: MutableMap<String, String> = mutableMapOf()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        url.openStream().use {
            val load = Yaml().load(it)
            if (load is Map<*, *>) {
                properties.clear()
                properties.putAll(flatten(load))
            }
        }
    }

    private fun flatten(map: Map<*, *>, prefix: String = ""): MutableMap<String, String> {
        val properties = mutableMapOf<String, String>()
        reduceInternal(properties, map, prefix)
        return properties
    }

    private fun reduceInternal(properties: MutableMap<String, String>, map: Map<*, *>, prefix: String = "") {
        map.forEach { key, value ->
            when (value) {
                is Map<*, *> -> reduceInternal(properties, value, prefix(prefix, key.toString()))
                else -> properties[prefix(prefix, key.toString())] = value.toString()
            }
        }
    }

    override fun reload() {
        loadProperties()
    }

    override fun get(key: String): String {
        return properties[key]?:""
    }
}