package com.jdiazcano.cfg4k.loaders

open class SystemPropertyConfigLoader : ConfigLoader {
    override fun reload() {
        // Nothing to do, the System.properties do the reload for us!
    }

    override fun get(key: String): String {
        return System.getProperty(key, "")
    }

}