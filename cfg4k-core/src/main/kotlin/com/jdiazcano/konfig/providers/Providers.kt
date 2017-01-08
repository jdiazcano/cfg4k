package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy

class Providers {
    companion object {
        fun cached(provider: ConfigProvider) = CachedConfigProvider(provider)

        fun overriden(loaders: Array<ConfigLoader>, reloadStrategy: ReloadStrategy? = null) = OverrideConfigProvider(loaders, reloadStrategy)

        fun proxy(configLoader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ProxyConfigProvider(configLoader, reloadStrategy)
    }
}