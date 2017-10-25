package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig

/**
 * EnvironmentConfigLoader will try to match the key to an environment variable. This will apply a series of
 * transformations before matching. Once the match is done, it will be cached until the next time. Once reload is called
 * it will clear the cache until the next get() call is done and the value is cached again.
 */
open class EnvironmentConfigLoader : ConfigLoader {
    protected val properties: MutableMap<String, String> = mutableMapOf()
    protected val transformations: MutableList<(String) -> String> = mutableListOf()

    init {
        addTransformer { key -> key.replace('.', '_') }
        addTransformer { key -> key.replace('.', '-') }
    }

    override fun get(key: String): ConfigObject? {
        // If we already have it in cache, we have to use it
        if (properties.containsKey(key)) {
            return properties[key]!!.toConfig()
        }

        transformations.forEach {
            val transformed = it(key).toUpperCase()
            val value = System.getenv()[transformed]
            if (value != null) {
                properties[key] = value
                return value.toConfig()
            }
        }

        return null
    }

    override fun reload() {
        properties.clear()
    }

    /**
     * Adds a transformer that will be performed once the get() method is called. This will transform the key to the
     * environment variable form (UPPER_CASE_FORM) and by default there are three transformers.
     *
     * 1- foo.bar to FOO-BAR
     * 2- foo.bar to FOO_BAR
     */
    fun addTransformer(transformer: (String) -> String) {
        transformations.add(transformer)
    }
}