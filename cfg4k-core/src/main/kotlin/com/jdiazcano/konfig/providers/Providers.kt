package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.loaders.ConfigLoader
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy

object Providers {
    fun cached(provider: ConfigProvider) = CachedConfigProvider(provider)

    fun overriden(vararg providers: ConfigProvider) = OverrideConfigProvider(*providers)

    fun proxy(configLoader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ProxyConfigProvider(configLoader, reloadStrategy)
}