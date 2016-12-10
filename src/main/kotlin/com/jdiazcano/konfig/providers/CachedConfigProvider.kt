package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.utils.Typable

@Suppress("UNCHECKED_CAST")
class CachedConfigProvider(val configProvider: ConfigProvider) : ConfigProvider {
    private val cache = mutableMapOf<String, Any>()

    override fun <T : Any> getProperty(name: String, type: Class<T>): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property = configProvider.getProperty(name, type)
            cache[name] = property
            return property
        }
    }

    override fun <T: Any> getProperty(name: String, type: Typable): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property: T = configProvider.getProperty(name, type)
            cache[name] = property
            return property
        }
    }

    override fun <T: Any> bind(prefix: String, type: Class<T>): T {
        // This is using %pre. in order to not collide with general properties
        val cachePrefix = "%pre.$prefix"
        if (cache.containsKey(cachePrefix)) {
            return cache[cachePrefix] as T
        } else {
            val property: T = configProvider.bind(prefix, type)
            cache[cachePrefix] = property
            return property
        }
    }

    override fun canParse(type: Class<out Any>) = configProvider.canParse(type)

    override fun reload() {
        cache.clear()
        configProvider.reload()
    }
}