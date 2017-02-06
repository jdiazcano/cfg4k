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

import com.jdiazcano.konfig.binders.ProxyBinder
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy
import com.jdiazcano.konfig.utils.SettingNotFound
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Type

class OverrideConfigProvider(
        vararg private val providers: ConfigProvider,
        private val reloadStrategy: ReloadStrategy? = null
) : ConfigProvider {

    override val binder = ProxyBinder()
    private val listeners = mutableListOf<() -> Unit>()
    private val cachedProviders = mutableMapOf<String, ConfigProvider>()

    init {
        reloadStrategy?.register(this)

        addReloadListener { cachedProviders.clear() }
    }

    override fun <T : Any> getProperty(name: String, type: Class<T>, default: T?): T {
        if (name in cachedProviders) {
            return cachedProviders[name]!!.getProperty(name, type, default)
        } else {
            for (provider in providers) {
                if (provider.contains(name)) {
                    cachedProviders[name] = provider
                    return provider.getProperty(name, type, default)
                }
            }
        }

        if (default != null) {
            return default
        } else {
            throw SettingNotFound("Setting $name was not found")
        }
    }

    override fun <T : Any> getProperty(name: String, type: Typable, default: T?): T {
        return getProperty(name, type.getType())
    }

    override fun <T : Any> getProperty(name: String, type: Type, default: T?): T {
        if (name in cachedProviders) {
            return cachedProviders[name]!!.getProperty(name, type, default)
        } else {
            for (provider in providers) {
                if (provider.contains(name)) {
                    cachedProviders[name] = provider
                    return provider.getProperty(name, type, default)
                }
            }
        }

        if (default != null) {
            return default
        } else {
            throw SettingNotFound("Setting $name was not found")
        }
    }

    override fun contains(name: String): Boolean {
        providers.forEach {
            if (it.contains(name)) {
                return true
            }
        }
        return false
    }

    override fun reload() {
        providers.forEach { it.reload() }
        listeners.forEach { it() }
    }

    override fun cancelReload(): Unit? {
        return reloadStrategy?.deregister(this)
    }

    override fun addReloadListener(listener: () -> Unit) {
        listeners.add(listener)
    }

}