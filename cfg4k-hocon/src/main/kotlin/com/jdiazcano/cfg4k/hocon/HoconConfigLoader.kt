package com.jdiazcano.cfg4k.hocon

import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import com.jdiazcano.cfg4k.sources.ConfigSource
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import java.io.File
import java.io.InputStreamReader
import java.net.URL

fun HoconConfigLoader(url: URL, options: ConfigParseOptions = ConfigParseOptions.defaults()): HoconConfigLoader {
    val loader = { ConfigFactory.parseURL(url, options) }
    return HoconConfigLoader(loader)
}

fun HoconConfigLoader(file: File, options: ConfigParseOptions = ConfigParseOptions.defaults()): HoconConfigLoader {
    val loader = { ConfigFactory.parseFileAnySyntax(file, options) }
    return HoconConfigLoader(loader)
}

fun HoconConfigLoader(resource: String, options: ConfigParseOptions = ConfigParseOptions.defaults()): HoconConfigLoader {
    val loader = { ConfigFactory.parseResourcesAnySyntax(resource, options) }
    return HoconConfigLoader(loader)
}

fun HoconConfigLoader(source: ConfigSource, options: ConfigParseOptions = ConfigParseOptions.defaults()): HoconConfigLoader {
    val loader = { ConfigFactory.parseReader(InputStreamReader(source.read()), options) }
    return HoconConfigLoader(loader)
}

fun HoconConfigLoader(config: Config, loader: () -> Config = { config }): HoconConfigLoader {
    return HoconConfigLoader(loader)
}

/**
 * Config loader that will handle the input with the HOCON (Human-Optimized Config Object Notation)
 */
open class HoconConfigLoader(protected val loader: () -> Config) : DefaultConfigLoader() {

    init {
        root = loader().toConfig()
    }

    override fun reload() {
        root = loader().toConfig()
    }

}