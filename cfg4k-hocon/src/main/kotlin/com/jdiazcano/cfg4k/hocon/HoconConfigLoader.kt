package com.jdiazcano.cfg4k.hocon

import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import java.io.File
import java.net.URL

/**
 * Config loader that will handle the input with the HOCON (Human-Optimized Config Object Notation)
 */
open class HoconConfigLoader : DefaultConfigLoader {

    protected val loader: () -> Config

    /**
     * Loads the configuration based on an URL
     */
    constructor(
            url: URL,
            options: ConfigParseOptions = ConfigParseOptions.defaults()
    ) {
        loader = { ConfigFactory.parseURL(url, options) }
        root = loader().asConfigObject()
    }

    /**
     * Loads the configuration based on a File
     */
    constructor(
            file: File,
            options: ConfigParseOptions = ConfigParseOptions.defaults()
    ) {
        loader = { ConfigFactory.parseFileAnySyntax(file, options) }
        root = loader().asConfigObject()
    }

    /**
     * Loads the configuration based on a reader
     */
    constructor(
            resource: String,
            options: ConfigParseOptions = ConfigParseOptions.defaults()
    ) {
        loader = { ConfigFactory.parseResourcesAnySyntax(resource, options) }
        root = loader().asConfigObject()
    }

    /**
     * Sets the config and the loader. The loader by default will just return the same loader so there will be no reload
     * feature unless a loader is given, the config object is not able to be reloaded mutably so it is not possible to
     * do it without a loader.
     */
    constructor(config: Config, loader: () -> Config = { config }) {
        root = loader().asConfigObject()
        this.loader = loader
    }

    override fun reload() {
        root = loader().asConfigObject()
    }

}