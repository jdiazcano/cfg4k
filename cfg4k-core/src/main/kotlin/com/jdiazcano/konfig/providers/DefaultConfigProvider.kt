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

import com.jdiazcano.konfig.binders.Binder
import com.jdiazcano.konfig.binders.ProxyBinder
import com.jdiazcano.konfig.loaders.ConfigLoader
import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.parsers.Parsers.findParser
import com.jdiazcano.konfig.parsers.Parsers.getClassedParser
import com.jdiazcano.konfig.parsers.Parsers.getParser
import com.jdiazcano.konfig.parsers.Parsers.getParseredParser
import com.jdiazcano.konfig.parsers.Parsers.isClassedParser
import com.jdiazcano.konfig.parsers.Parsers.isParser
import com.jdiazcano.konfig.parsers.Parsers.isParseredParser
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.SettingNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
open class DefaultConfigProvider(
        private val configLoader: ConfigLoader,
        private val reloadStrategy: ReloadStrategy? = null,
        override val binder: Binder = ProxyBinder()
): ConfigProvider {

    private val listeners: MutableList<() -> Unit> = mutableListOf()

    init {
        reloadStrategy?.register(this)
    }

    override fun <T: Any> getProperty(name: String, type: Class<T>, default: T?): T {
        // There is no way that this has a generic parsers because the class actually removes that possiblity
        if (isParser(type)) {
            val value = configLoader.get(name)
            if (value != null) {
                return getParser(type).parse(value)
            } else {
                if (default != null) {
                    return default
                } else {
                    throw SettingNotFound("Setting $name was not found")
                }
            }
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun <T: Any> getProperty(name: String, type: Typable, default: T?): T {
        return getProperty(name, type.getType(), default)
    }

    override fun <T: Any> getProperty(name: String, type: Type, default: T?): T {
        val rawType = TargetType(type).rawTargetType()

        val value = configLoader.get(name)
        if (value != null) {
            if (isParseredParser(rawType)) {
                val parser = getParseredParser(rawType) as Parser<T>
                val superType = TargetType(type).getParameterizedClassArguments()[0]
                return parser.parse(value, superType, findParser(superType) as Parser<T>)
            } else if (isClassedParser(rawType.superclass)) {
                val parser = getClassedParser(rawType.superclass!!) as Parser<T>
                return parser.parse(value, rawType as Class<T>)
            } else if (isParser(rawType)) {
                return getParser(rawType).parse(value) as T
            }
            throw ParserClassNotFound("Parser for class $type was not found")
        } else {
            if (default != null) {
                return default
            } else {
                throw SettingNotFound("Setting $name was not found")
            }
        }
    }

    override fun contains(name: String) = configLoader.get(name) != null

    override fun cancelReload() = reloadStrategy?.deregister(this)

    override fun reload() {
        configLoader.reload()
        listeners.forEach { it() } // call listeners
    }

    override fun addReloadListener(listener: () -> Unit) {
        listeners.add(listener)
    }

}
