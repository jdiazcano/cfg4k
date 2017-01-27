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

@file:Suppress("UNCHECKED_CAST")

package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.Binder
import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binders.BindingInvocationHandler
import com.jdiazcano.konfig.binders.ProxyBinder
import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.parsers.Parsers
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Proxy
import java.lang.reflect.Type

class OverrideConfigProvider(
        private val loaders: Array<ConfigLoader>,
        private val reloadStrategy: ReloadStrategy? = null,
        override val binder: Binder = ProxyBinder()
) : ConfigProvider {

    private val listeners = mutableListOf<() -> Unit>()
    private val cachedLoaders = mutableMapOf<String, ConfigLoader>()

    init {
        reloadStrategy?.register(this)

        addReloadListener { cachedLoaders.clear() }
    }

    override fun <T : Any> getProperty(name: String, type: Class<T>): T {
        val value = getValueAndCacheLoader(name)

        // There is no way that this has a generic parsers because the class actually removes that possibility
        if (Parsers.isParser(type)) {
            return Parsers.getParser(type).parse(value)
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    private fun getValueAndCacheLoader(name: String): String {
        var value: String = ""
        if (name in cachedLoaders) {
            value = cachedLoaders[name]!!.get(name)
        } else {
            for (loader in loaders) {
                val internalValue = loader.get(name)
                if (internalValue != "") {
                    value = internalValue
                    cachedLoaders[name] = loader
                    break
                }
            }
        }
        return value
    }

    override fun <T : Any> getProperty(name: String, type: Typable): T {
        return getProperty(name, type.getType())
    }

    override fun <T : Any> getProperty(name: String, type: Type): T {
        var value: String = ""
        if (name in cachedLoaders) {
            value = cachedLoaders[name]!!.get(name)
        } else {
            for (loader in loaders) {
                val internalValue = loader.get(name)
                if (internalValue != "") {
                    value = internalValue
                    cachedLoaders[name] = loader
                    break
                }
            }
        }

        val rawType = TargetType(type).rawTargetType()
        if (Parsers.isParseredParser(rawType)) {
            val parser = Parsers.getParseredParser(rawType) as Parser<T>
            val superType = TargetType(type).getParameterizedClassArguments()[0]
            return parser.parse(value, superType, Parsers.findParser(superType) as Parser<T>)
        } else if (Parsers.isClassedParser(rawType.superclass)) {
            val parser = Parsers.getClassedParser(rawType.superclass!!) as Parser<T>
            return parser.parse(value, rawType as Class<T>)
        } else if (Parsers.isParser(rawType)) {
            return Parsers.getParser(rawType).parse(value) as T
        }
        throw ParserClassNotFound("Parser for class $type was not found")
    }

    override fun reload() {
        loaders.forEach { it.reload() }
        listeners.forEach { it() }
    }

    override fun cancelReload(): Unit? {
        return reloadStrategy?.deregister(this)
    }

    override fun addReloadListener(listener: () -> Unit) {
        listeners.add(listener)
    }

}