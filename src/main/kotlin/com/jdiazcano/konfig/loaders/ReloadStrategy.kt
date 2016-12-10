package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigProvider

interface ReloadStrategy {
    fun register(configProvider: ConfigProvider)

    fun deregister(configProvider: ConfigProvider)
}
