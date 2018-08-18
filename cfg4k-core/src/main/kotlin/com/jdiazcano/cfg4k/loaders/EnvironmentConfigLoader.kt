package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig

/**
 * EnvironmentConfigLoader will try to match the key to an environment variable. This will apply a series of
 * transformations before matching.
 */
open class EnvironmentConfigLoader : DefaultConfigLoader(System.getenv().toConfig()) {
    protected val transformations: MutableList<(String) -> String> = mutableListOf()

    init {
        addTransformer { key -> key.replace('.', '_') }
        addTransformer { key -> key.replace('.', '-') }
    }

    override fun get(key: String): ConfigObject? {
        transformations.forEach {
            val transformed = it(key).toUpperCase()
            val value = super.get(transformed)
            if (value != null) {
                return value
            }
        }

        return null
    }

    override fun reload() {
        root = System.getenv().toConfig()
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