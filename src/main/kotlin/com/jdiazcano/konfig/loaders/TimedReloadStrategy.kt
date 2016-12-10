package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class TimedReloadStrategy(val time: Long, val unit: TimeUnit) : ReloadStrategy {

    private lateinit var reloadTimer: Timer

    override fun register(configLoader: ConfigLoader) {
        reloadTimer = timer("TimeReloadStrategy", true, unit.toMillis(time), unit.toMillis(time)) {
            configLoader.reload()
        }
    }

    override fun deregister(configLoader: ConfigLoader) {
        reloadTimer.cancel()
    }
}