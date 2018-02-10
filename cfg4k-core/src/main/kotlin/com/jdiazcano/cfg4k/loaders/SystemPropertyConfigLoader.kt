package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig

open class SystemPropertyConfigLoader : DefaultConfigLoader(System.getProperties().toConfig()) {
    override fun reload() {
        // Nothing to do, the System.properties do the reload for us!
    }

    override fun get(key: String): ConfigObject? {
        return System.getProperty(key, null)?.toConfig()
    }

}

