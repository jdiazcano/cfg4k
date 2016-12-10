package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader

interface ReloadStrategy {
    fun register(configLoader: ConfigLoader)

    fun deregister(configLoader: ConfigLoader)
}
