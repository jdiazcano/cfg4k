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

package com.jdiazcano.cfg4k.providers

import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.reloadstrategies.ReloadStrategy
import com.jdiazcano.cfg4k.utils.SettingNotFound
import java.lang.reflect.Type

class OverrideConfigProvider(
        private vararg val providers: ConfigProvider,
        private val reloadStrategy: ReloadStrategy? = null
) : ConfigProvider {

    override val binder = ProxyBinder()
    private val listeners = mutableListOf<() -> Unit>()
    private val errorReloadListeners = mutableListOf<(Exception) -> Unit>()
    private val cachedProviders = mutableMapOf<String, ConfigProvider>()

    init {
        reloadStrategy?.register(this)

        addReloadListener { cachedProviders.clear() }
    }

    override fun load(name: String): ConfigObject? {
        if (name in cachedProviders) {
            return cachedProviders[name]!!.load(name)
        } else {
            for (provider in providers) {
                if (provider.contains(name)) {
                    cachedProviders[name] = provider
                    return provider.load(name)
                }
            }
        }

        return null
    }

    override fun <T : Any> get(name: String, type: Type, default: T?): T {
        if (name in cachedProviders) {
            return cachedProviders[name]!!.get(name, type, default)
        } else {
            for (provider in providers) {
                if (provider.contains(name)) {
                    cachedProviders[name] = provider
                    return provider.get(name, type, default)
                }
            }
        }

        if (default != null) {
            return default
        } else {
            throw SettingNotFound(name)
        }
    }

    override fun <T> getOrNull(name: String, type: Type, default: T?): T? {
        if (name in cachedProviders) {
            return cachedProviders[name]!!.getOrNull(name, type, default)
        } else {
            for (provider in providers) {
                if (provider.contains(name)) {
                    cachedProviders[name] = provider
                    return provider.getOrNull(name, type, default)
                }
            }
        }

        return default
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
        try {
            providers.forEach { it.reload() }
            listeners.forEach { it() }
        } catch (e: Exception) {
            errorReloadListeners.forEach { it(e) }
        }

    }

    override fun cancelReload(): Unit? {
        return reloadStrategy?.deregister(this)
    }

    override fun addReloadListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    override fun addReloadErrorListener(listener: (Exception) -> Unit) {
        errorReloadListeners.add(listener)
    }

}