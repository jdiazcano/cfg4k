package com.jdiazcano.cfg4k.providers

import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.reloadstrategies.ReloadStrategy

object Providers {
    fun cached(provider: ConfigProvider) = CachedConfigProvider(provider)

    fun overriden(vararg providers: ConfigProvider) = OverrideConfigProvider(*providers)

    fun proxy(configLoader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ProxyConfigProvider(configLoader, reloadStrategy)
}