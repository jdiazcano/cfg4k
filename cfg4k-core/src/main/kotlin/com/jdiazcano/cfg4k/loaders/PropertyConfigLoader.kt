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
import com.jdiazcano.cfg4k.sources.ConfigSource
import mu.KotlinLogging
import java.util.*
import kotlin.collections.ArrayList

private val logger = KotlinLogging.logger {}

open class PropertyConfigLoader(
        private val source: ConfigSource
) : DefaultConfigLoader() {

    init {
        loadProperties()
    }

    private fun loadProperties() {
        root = Properties().apply { load(source.read()) }.toConfig()
    }

    override fun reload() {
        loadProperties()
    }

}

fun Properties.toConfig(): ConfigObject {
    val map = mutableMapOf<String, Any>()
    map { (key, value) ->
        val keyAsString = key.toString()
        val keys = keyAsString.split('.')

        if (keys.size == 1) {
            val (number, cleanKey) = findNumbers(keyAsString)

            if (number == null) {
                if (map[cleanKey] != null) {
                    logger.warn { "$key will be overridden as it is defined twice." }
                }

                map[cleanKey] = value
            } else {
                val list = map.getOrPut(cleanKey) { arrayListOf<Any>() } as MutableList<Any>
                list += value
            }
        } else {
            val parentValue = keys.dropLast(1).fold(map as Any) { m, k ->
                m as MutableMap<String, Any>

                val (number, cleanKey) = findNumbers(k)
                val innerValue = m.getOrPut(cleanKey) {
                    if (number == null) {
                        mutableMapOf<String, Any>()
                    } else {
                        arrayListOf<Any>(mutableMapOf<String, Any>())
                    }
                }

                if (number == null) {
                    if (innerValue is String) {
                        logger.warn { "Key ($key=$innerValue) has overridden another value used as root" }
                        mutableMapOf<String, Any>()
                    } else {
                        innerValue
                    }
                } else {
                    innerValue as ArrayList<Any>
                    (innerValue.size..number).forEach { _ ->
                        innerValue.add(mutableMapOf<String, Any>())
                    }
                    innerValue[number]
                }
            }

            setValue(parentValue, keys, value)
        }
    }

    return map.toConfig()
}

private fun setValue(parentValue: Any, keys: List<String>, value: Any) {
    val (number, key) = findNumbers(keys.last())
    parentValue as MutableMap<String, Any>
    if (number == null) {
        if (parentValue[key] == null) {
            parentValue[key] = value
        } else {
            logger.warn { "Key '${keys.joinToString(".")}' was ignored as it was going to override another value." }
        }
    } else {
        val list = parentValue.getOrPut(key) { mutableListOf<Any>() } as MutableList<Any>
        list.add(value)
    }
}