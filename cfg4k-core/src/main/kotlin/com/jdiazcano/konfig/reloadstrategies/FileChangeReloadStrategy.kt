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

import com.jdiazcano.konfig.providers.ConfigProvider
import java.io.File
import java.nio.file.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.concurrent.timer

/**
 * This reload strategy will reload the config provider when the timer ticks. A TimeUnit is used which will be
 * translated to milliseconds.
 */
class FileChangeReloadStrategy(val file: Path) : ReloadStrategy {

    private val watcher = FileSystems.getDefault().newWatchService()
    private var watching = false
    private lateinit var thread: Thread

    constructor(file: String): this(Paths.get(file))

    constructor(file: File): this(file.toPath())

    init {
        if (file.toFile().isDirectory) {
            throw IllegalArgumentException("$file must not be a directory")
        }
    }

    override fun register(configProvider: ConfigProvider) {
        val fileWatcher = file.toAbsolutePath().parent.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
        watching = true
        thread = thread(start = true, isDaemon = true) {
            while (watching) {
                try {
                    val key = watcher.take()
                    fileWatcher.pollEvents().forEach { event ->
                        val fileName = event.context() as Path
                        if (fileName == file) {
                            configProvider.reload()
                        }
                    }

                    val valid = key.reset()
                    if (!valid) {
                        watching = false
                    }
                } catch (exception: InterruptedException) {
                    watching = false
                }
            }
        }
    }

    override fun deregister(configProvider: ConfigProvider) {
        thread.interrupt()
    }
}