package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.loaders.ConfigLoader
import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.loaders.ReloadStrategy

class Providers {
    companion object {
        fun cached(provider: ConfigProvider) = CachedConfigProvider(provider)

        fun overriden(loaders: Array<ConfigLoader>, reloadStrategy: ReloadStrategy? = null) = OverrideConfigProvider(loaders, reloadStrategy)

        fun proxy(configLoader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ProxyConfigProvider(configLoader, reloadStrategy)
    }
}