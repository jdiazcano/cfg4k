package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader

open class EnvironmentConfigLoader : ConfigLoader {
    protected val properties: MutableMap<String, String> = mutableMapOf()
    protected val transformations: MutableList<(String) -> String> = mutableListOf()

    init {
        /*
        I leave this here in case we want to have a transformer for camelCase
        addTransformer( { key ->
            key.replace("([a-z])([A-Z])".toRegex(), { result -> "${result.groups[1]!!.value}.${result.groups[2]!!.value}" })
        })*/
        addTransformer( { key -> key.replace('_', '.') } )
        addTransformer( { key -> key.replace('-', '.') } )

        loadProperties()
    }

    private fun loadProperties() {
        properties.clear()
        System.getenv().forEach { key, value ->
            properties[sanitize(key)] = value
        }
    }

    private fun sanitize(key: String): String {
        var sanitizedKey = key
        transformations.forEach { transformation ->
            sanitizedKey = transformation.invoke(sanitizedKey)
        }

        return sanitizedKey.toLowerCase()
    }

    override fun get(key: String): String {
        return properties[key]?:""
    }

    override fun reload() {
        loadProperties()
    }

    /**
     * Adds a transformer in order to transform an ENVIRONMENT_VARIABLE into a environment.variable property setting.
     * So later this will be the one that you will be using in order to retrieve it from the provider/loader.
     */
    fun addTransformer(transformer: (String) -> String) {
        transformations.add(transformer)
    }
}