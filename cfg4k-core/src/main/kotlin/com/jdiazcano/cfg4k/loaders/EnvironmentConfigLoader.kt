package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject

private val DEFAULT_TRANSFORMERS = mutableListOf<(String) -> String>(
        { key -> key.replace('_', '.') },
        { key -> key.replace('-', '.') }
)

/**
 * EnvironmentConfigLoader will try to match the key to an environment variable. This will apply a series of
 * transformations before matching.
 */
open class EnvironmentConfigLoader(
        protected val transformations: MutableList<(String) -> String> = DEFAULT_TRANSFORMERS
) : DefaultConfigLoader(System.getenv().transformice(transformations).toProperties().toConfig()) {

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
        root = System.getenv().transformice(transformations).toProperties().toConfig()
    }
}

private fun Map<String, String>.transformice(transformations: MutableList<(String) -> String>) = map { (key, value) ->
    transformations.fold(key) { transformedKey, transformer -> transformer(transformedKey) } to value
}.toMap()