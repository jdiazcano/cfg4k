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

import com.jdiazcano.cfg4k.binders.Binder
import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.binders.createCollection
import com.jdiazcano.cfg4k.binders.toMutableCollection
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.isList
import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.parsers.Parsers.findParser
import com.jdiazcano.cfg4k.parsers.Parsers.isParseable
import com.jdiazcano.cfg4k.reloadstrategies.ReloadStrategy
import com.jdiazcano.cfg4k.utils.ParserClassNotFound
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.jdiazcano.cfg4k.utils.TargetType
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
open class DefaultConfigProvider(
        private val configLoader: ConfigLoader,
        private val reloadStrategy: ReloadStrategy? = null,
        override val binder: Binder = ProxyBinder()
) : ConfigProvider {

    private val listeners: MutableList<() -> Unit> = mutableListOf()
    private val errorReloadListeners: MutableList<(Exception) -> Unit> = mutableListOf()

    init {
        reloadStrategy?.register(this)
    }

    override fun <T : Any> get(name: String, type: Class<T>, default: T?): T {
        // There is no way that this has a generic parsers because the class actually removes that possibility
        return if (type.isParseable()) {
            val value = configLoader.get(name)
            if (value != null) {
                type.findParser().parse(value) as T
            } else {
                default ?: throw SettingNotFound("Setting $name was not found")
            }
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun <T : Any> get(name: String, type: Type, default: T?): T {
        val targetType = TargetType(type)
        val rawType = targetType.rawTargetType()

        val value = configLoader.get(name)
        if (value != null) {
            if (rawType.isParseable()) {
                val superType = targetType.getParameterizedClassArguments().firstOrNull()
                val classType = superType ?: rawType
                return rawType.findParser().parse(value, classType, superType?.findParser()) as T
            } else if (value.isList()) {
                val superType = targetType.getParameterizedClassArguments().firstOrNull()
                val classType = superType ?: rawType
                val collection = createCollection(rawType)
                toMutableCollection(value, classType, collection, name, this, name)
                return collection as T
            }
            throw ParserClassNotFound("Parser for class $type was not found")
        } else {
            return default ?: throw SettingNotFound("Setting $name was not found")
        }
    }

    override fun load(name: String): ConfigObject? {
        return configLoader.get(name)
    }

    override fun <T> getOrNull(name: String, type: Class<T>, default: T?): T? {
        // There is no way that this has a generic parsers because the class actually removes that possibility
        if (type.isParseable()) {
            val value = configLoader.get(name)
            if (value != null) {
                return type.findParser().parse(value) as T
            } else {
                return default
            }
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun <T> getOrNull(name: String, type: Type, default: T?): T? {
        val targetType = TargetType(type)
        val rawType = targetType.rawTargetType()

        val value = configLoader.get(name)
        if (value != null) {
            if (rawType.isParseable()) {
                val superType = targetType.getParameterizedClassArguments().firstOrNull()
                val classType = superType ?: rawType
                return rawType.findParser().parse(value, classType, superType?.findParser()) as T
            }
            throw ParserClassNotFound("Parser for class $type was not found")
        } else {
            return default
        }
    }

    override fun contains(name: String) = configLoader.get(name) != null

    override fun cancelReload() = reloadStrategy?.deregister(this)

    override fun reload() {
        try {
            configLoader.reload()
            listeners.forEach { it() } // call listeners
        } catch (e: Exception) {
            errorReloadListeners.forEach { it(e) }
        }
    }

    override fun addReloadListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    override fun addReloadErrorListener(listener: (Exception) -> Unit) {
        errorReloadListeners.add(listener)
    }

}
