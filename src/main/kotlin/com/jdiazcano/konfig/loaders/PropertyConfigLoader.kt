package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.utils.Reloadable

open class PropertyConfigLoader(
        private val lines: List<String>
): ConfigLoader, Reloadable {

    val properties: MutableMap<String, String> = mutableMapOf()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        lines.forEach { line ->
            val split = line.split('=')
            properties[split[0]] = split[1]
        }
    }

    override fun reload() {
        loadProperties()
    }

    override fun get(key: String): String = properties[key]?: ""

}
