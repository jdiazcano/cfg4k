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

package com.jdiazcano.konfig.loaders

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.concurrent.TimeUnit

class TimedReloadStrategyTest : Spek({
    val text = """{
  "a": "%reload1",
  "c": "%reload2",
  "nested": {
    "a": "reloaded nestedb"
  }
}"""
    describe("a timed reloadable json config loader") {
        it("defaultconfigprovider test") {
            val file = File("timedreloadedfile.json")
            file.createNewFile()
            file.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val provider = DefaultConfigProvider(JsonConfigLoader(file.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS))
            checkProvider(file, provider, text)
        }

        it("cacheddefaultconfigprovider test") {
            val cachedfile = File("cachedtimedreloadedfile.json")
            cachedfile.createNewFile()
            cachedfile.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val cachedProvider = CachedConfigProvider(DefaultConfigProvider(JsonConfigLoader(cachedfile.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS)))
            checkProvider(cachedfile, cachedProvider, text)
        }
    }

})

private fun checkProvider(file: File, provider: ConfigProvider, text: String) {
    provider.getProperty("a", String::class.java).should.be.equal("b")
    provider.getProperty("c", String::class.java).should.be.equal("d")
    var lastReload = 1
    val lastIteration = 7
    for (i in 1..10) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration (8-1)
        }
        file.writeText(text.replace("%reload1", "b$lastReload").replace("%reload2", "d$lastReload"))
        Thread.sleep(1500)
        provider.getProperty("a", String::class.java).should.be.equal("b$lastReload")
        provider.getProperty("c", String::class.java).should.be.equal("d$lastReload")
        lastReload++
    }
    file.delete()
}
