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

package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.utils.Reloadable
import com.jdiazcano.konfig.utils.asLines
import java.net.URL

open class PropertyConfigLoader(
        private val url: URL
): ConfigLoader, Reloadable {

    val properties: MutableMap<String, String> = mutableMapOf()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        url.asLines().forEach { line ->
            val split = line.split('=')
            properties[split[0]] = split[1]
        }
    }

    override fun reload() {
        loadProperties()
    }

    override fun get(key: String): String = properties[key]?: ""

}
