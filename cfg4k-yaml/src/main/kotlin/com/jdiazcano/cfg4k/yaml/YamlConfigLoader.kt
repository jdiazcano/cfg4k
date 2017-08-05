package com.jdiazcano.cfg4k.yaml

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import org.yaml.snakeyaml.Yaml
import java.net.URL

class YamlConfigLoader(private val url: URL) : DefaultConfigLoader() {

    init {
        loadProperties()
    }

    private fun loadProperties() {
        url.openStream().use {
            val load = Yaml().load(it) as Map<String, Any>
            root = load.toConfig()
        }
    }

    override fun reload() {
        loadProperties()
    }

}