package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class TimedReloadStrategy(val time: Long, val unit: TimeUnit) : ReloadStrategy {

    private lateinit var reloadTimer: Timer

    override fun register(configProvider: ConfigProvider) {
        reloadTimer = timer("TimeReloadStrategy", true, unit.toMillis(time), unit.toMillis(time)) {
            configProvider.reload()
        }
    }

    override fun deregister(configProvider: ConfigProvider) {
        reloadTimer.cancel()
    }
}