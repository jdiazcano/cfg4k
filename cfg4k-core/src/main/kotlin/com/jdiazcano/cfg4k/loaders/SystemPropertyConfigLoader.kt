package com.jdiazcano.cfg4k.loaders

open class SystemPropertyConfigLoader : DefaultConfigLoader(System.getProperties().toConfig()) {
    override fun reload() {
        root = System.getProperties().toConfig()
    }
}

