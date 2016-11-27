package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader

class PropertyConfigLoader(
        file: List<String>
): ConfigLoader {
    val properties: MutableMap<String, String> = mutableMapOf()

    init {
        file.forEach { line ->
            val splitted = line.split('=')
            properties[splitted[0]] = splitted[1]
        }
    }

    override fun get(key: String): String = properties[key]?: ""

}
