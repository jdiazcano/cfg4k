package com.jdiazcano.cfg4k.yaml

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import com.jdiazcano.cfg4k.sources.ConfigSource
import org.yaml.snakeyaml.Yaml

class YamlConfigLoader(private val configSource: ConfigSource) : DefaultConfigLoader() {

    init {
        loadProperties()
    }

    private fun loadProperties() {
        configSource.read().use {
            val load = Yaml().load(it) as Map<String, Any>
            root = load.toConfig()
        }
    }

    override fun reload() {
        loadProperties()
    }

}