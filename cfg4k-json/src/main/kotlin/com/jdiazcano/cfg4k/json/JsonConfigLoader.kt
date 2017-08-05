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
package com.jdiazcano.cfg4k.json

import com.beust.klaxon.Parser
import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import java.net.URL

open class JsonConfigLoader(
        private val url: URL
) : DefaultConfigLoader() {

    protected val parser = Parser()

    init {
        loadProperties()
    }

    override fun reload() {
        loadProperties()
    }

    protected fun loadProperties() {
        url.openStream().use {
            root = parser.asConfigObjectFromJson(it)
        }
    }

}
