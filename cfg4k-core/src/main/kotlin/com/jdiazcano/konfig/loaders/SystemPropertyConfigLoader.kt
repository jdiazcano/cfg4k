package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader

open class SystemPropertyConfigLoader : ConfigLoader {
    protected val properties: MutableMap<String, String> = mutableMapOf()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        properties.clear()
        System.getProperties().forEach { key, value ->
            properties[key.toString()] = value.toString()
        }
    }

    override fun get(key: String): String {
        return properties[key]?:""
    }

    override fun reload() {
        loadProperties()
    }

}