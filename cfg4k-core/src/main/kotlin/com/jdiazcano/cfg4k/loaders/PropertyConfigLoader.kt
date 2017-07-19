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

package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import java.net.URL
import java.util.*

open class PropertyConfigLoader(
        private val url: URL
): ConfigLoader {

    val properties = Properties()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        properties.clear()
        url.openStream().use {
            properties.load(it)
        }
    }

    override fun reload() {
        loadProperties()
    }

    override fun get(key: String): String? = properties.getProperty(key)

}

fun String.toURL() = URL(this)

fun URL.asProperties(): Properties {
    val properties = Properties()
    openStream().use {
        properties.load(it)
    }
    return properties
}

fun Properties.toConfig(): ConfigObject {
    val map = mutableMapOf<String, Any>()
    map { (key, value) ->
        val keys = key.toString().split('.')

        if (keys.size == 1) {
            if (map[key.toString()] != null) {
                throw IllegalArgumentException("$key is defined twice")
            }

            map[key.toString()] = value
        } else {
            val valueMap = keys.dropLast(1).fold(map) { m, k ->
                if (m[k] != null && m[k] !is Map<*, *>) {
                    throw IllegalArgumentException("")
                }

                // TODO Think if on how to make arrays. I think it should be discouraged but...
                m.getOrPut(k) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
            valueMap[keys.last()] = value
        }
    }
    return map.toConfig()
}
