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

package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.utils.Typable

@Suppress("UNCHECKED_CAST")
class CachedConfigProvider(val configProvider: ConfigProvider) : ConfigProvider by configProvider {
    private val cache = mutableMapOf<String, Any>()

    init {
        configProvider.addReloadListener { cache.clear() }
    }

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

}