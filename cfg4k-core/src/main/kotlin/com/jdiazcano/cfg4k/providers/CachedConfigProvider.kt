/*
 * Copyright 2015-2016 Javier Díaz-Cano Martín-Albo (javierdiazcanom@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jdiazcano.cfg4k.providers

import java.lang.reflect.Type

/**
 * This config provider will cache the calls so binding and property lookup is not done everytime. Reloading this
 * provider will clear the cache so things will have to be binded again.
 */
@Suppress("UNCHECKED_CAST")
class CachedConfigProvider(private val configProvider: ConfigProvider) : ConfigProvider by configProvider {
    private val cache = mutableMapOf<String, Any>()

    init {
        configProvider.addReloadListener { cache.clear() }
    }

    override fun <T : Any> get(name: String, type: Class<T>, default: T?): T {
        return if (cache.containsKey(name)) {
            cache[name] as T
        } else {
            val property = configProvider.get(name, type, default)
            cache[name] = property
            property
        }
    }

    override fun <T : Any> get(name: String, type: Type, default: T?): T {
        return if (cache.containsKey(name)) {
            cache[name] as T
        } else {
            val property: T = configProvider.get(name, type, default)
            cache[name] = property
            property
        }
    }

    override fun <T : Any> bind(prefix: String, type: Class<T>): T {
        // This is using %pre. in order to not collide with general properties
        val cachePrefix = "%pre.$prefix"
        return if (cache.containsKey(cachePrefix)) {
            cache[cachePrefix] as T
        } else {
            val property: T = configProvider.bind(prefix, type)
            cache[cachePrefix] = property
            property
        }
    }

}