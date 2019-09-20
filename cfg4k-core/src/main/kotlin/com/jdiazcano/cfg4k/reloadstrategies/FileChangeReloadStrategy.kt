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
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import kotlin.concurrent.thread

/**
 * This reload strategy will reload the config provider when the timer ticks. A TimeUnit is used which will be
 * translated to milliseconds.
 */
class FileChangeReloadStrategy(val file: Path) : ReloadStrategy {

    private val watcher = FileSystems.getDefault().newWatchService()
    private var watching = false
    private lateinit var thread: Thread

    constructor(file: String) : this(Paths.get(file))

    constructor(file: File) : this(file.toPath())

    init {
        if (file.toFile().isDirectory) {
            throw IllegalArgumentException("$file must not be a directory")
        }
    }

    override fun register(configProvider: ConfigProvider) {
        // if its a symlink, then we should also watch symlinks for changes, this supports Kubernetes-style ConfigMap resources e.g.
        //   configfile -> ..data/configfile
        //   ..data -> ..2019_09_20_05_25_13.543205648
        //   ..2019_09_20_05_25_13.543205648/configfile
        // Here, Kubernetes creates a new timestamped directory when the configmap changes, and just modifies the ..data symlink to point to it
        // FileWatcher raises symlink changes (e.g. overwrite an existing link target on Linux via `ln -sfn`) as ENTRY_CREATE
        // we don't use toRealPath() here, because we *want* the parent the file appears to be in, not the file's real parent
        val fileWatcher = file.toAbsolutePath().parent.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY)

        watching = true
        thread = thread(start = true, isDaemon = true) {
            while (watching) {
                try {
                    val key = watcher.take()
                    fileWatcher.pollEvents().forEach { event ->
                        if(event.kind() == StandardWatchEventKinds.OVERFLOW) return@forEach

                        val fileName = event.context() as Path
                        // if any entry in the chain of symbolic links leading to the actual file, including the actual
                        // file itself, has been created/modified, reload
                        val linkChain = generateSequence(file) {
                            if (Files.isSymbolicLink(it)) {
                                it.parent.resolve(Files.readSymbolicLink(it).first())
                            } else null
                        }.toList()

                        if (linkChain.contains(file.parent.resolve(fileName))) {
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
