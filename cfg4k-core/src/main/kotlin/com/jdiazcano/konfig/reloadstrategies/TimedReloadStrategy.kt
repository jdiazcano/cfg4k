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

package com.jdiazcano.konfig.reloadstrategies

import com.jdiazcano.konfig.ConfigProvider
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

/**
 * This reload strategy will reload the config provider when the timer ticks. A TimeUnit is used which will be
 * translated to milliseconds.
 */
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