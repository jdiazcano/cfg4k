package com.jdiazcano.cfg4k.loaders

import java.util.*

private val DEFAULT_TRANSFORMERS = mutableListOf<(String) -> String>(
        { key -> key.replace('_', '.') },
        { key -> key.replace('-', '.') },
        { key -> key.toLowerCase(Locale.getDefault()) }
)

/**
 * EnvironmentConfigLoader will try to match the key to an environment variable. This will apply a series of
 * transformations before matching.
 */
open class EnvironmentConfigLoader(
        protected val transformations: MutableList<(String) -> String> = DEFAULT_TRANSFORMERS
) : DefaultConfigLoader(System.getenv().transformice(transformations).toProperties().toConfig()) {

    override fun reload() {
        root = System.getenv().transformice(transformations).toProperties().toConfig()
    }
}

private fun Map<String, String>.transformice(transformations: MutableList<(String) -> String>) = map { (key, value) ->
    transformations.fold(key) { transformedKey, transformer ->
        transformer(transformedKey)
    } to value
}.toMap()