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

package com.jdiazcano.cfg4k.reloadstrategies

import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy.Mode.FIXED_DELAY
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy.Mode.FIXED_RATE
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * This reload strategy will reload the config provider when the timer ticks. A TimeUnit is used which will be
 * translated to milliseconds.
 */
class TimedReloadStrategy(private val time: Long,
                          private val unit: TimeUnit,
                          private val mode: Mode = FIXED_RATE) : ReloadStrategy {
    enum class Mode {
        FIXED_RATE, FIXED_DELAY
    }

    private val executor: ScheduledExecutorService by lazy {
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "TimeReloadStrategy").also { it.isDaemon = true }
        }
    }
    private val reloadTasks = mutableMapOf<ConfigProvider, ScheduledFuture<*>>()

    override fun register(configProvider: ConfigProvider) {
        reloadTasks.computeIfAbsent(configProvider) { cp ->
            val reload = {
                try {
                    cp.reload()
                } catch (ignored: Exception) {
                }
            }
            when (mode) {
                FIXED_RATE -> executor.scheduleAtFixedRate(reload, 0, time, unit)
                FIXED_DELAY -> executor.scheduleWithFixedDelay(reload, 0, time, unit)
            }
        }
    }

    override fun deregister(configProvider: ConfigProvider) {
        reloadTasks[configProvider]?.cancel(true)
    }
}
