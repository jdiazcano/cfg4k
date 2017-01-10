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

import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.ProxyConfigProvider
import com.jdiazcano.konfig.providers.OverrideConfigProvider
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
            val provider = ProxyConfigProvider(JsonConfigLoader(file.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS))
            checkProvider(file, provider, text)
        }

        it("cacheddefaultconfigprovider test") {
            val cachedfile = File("cachedtimedreloadedfile.json")
            cachedfile.createNewFile()
            cachedfile.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val cachedProvider = CachedConfigProvider(ProxyConfigProvider(JsonConfigLoader(cachedfile.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS)))
            checkProvider(cachedfile, cachedProvider, text)
        }

        it("overrideconfigprovider test") {
            val overrideFile = File("override.json")
            overrideFile.createNewFile()
            overrideFile.writeText(text.replace("%reload1", "overrideb").replace(",\n  \"c\": \"%reload2\"", ""))

            val normalFile = File("normal.json")
            normalFile.createNewFile()
            normalFile.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val provider = OverrideConfigProvider(
                    arrayOf(
                            JsonConfigLoader(overrideFile.toURI().toURL()),
                            JsonConfigLoader(normalFile.toURI().toURL())
                    ),
                    TimedReloadStrategy(1, TimeUnit.SECONDS)
            )
            checkProvider(overrideFile, provider, text, true)
            normalFile.delete()
        }
    }

})

private fun checkProvider(file: File, provider: ConfigProvider, text: String, overriden: Boolean = false) {
    val binded = provider.bind<Normal>("")
    if (overriden) {
        provider.getProperty("a", String::class.java).should.be.equal("overrideb")
        binded.a().should.be.equal("overrideb")
    } else {
        provider.getProperty("a", String::class.java).should.be.equal("b")
        binded.a().should.be.equal("b")
    }
    provider.getProperty("c", String::class.java).should.be.equal("d")
    binded.c().should.be.equal("d")
    var lastReload = 1
    val lastIteration = 3
    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration (8-1)
        }
        if (overriden) {
            file.writeText(text.replace("%reload1", "overrideb$lastReload").replace(",\n  \"c\": \"%reload2\"", ""))
        } else {
            file.writeText(text.replace("%reload1", "b$lastReload").replace("%reload2", "d$lastReload"))
        }
        Thread.sleep(1500)
        if (overriden) {
            provider.getProperty("a", String::class.java).should.be.equal("overrideb$lastReload")
            provider.getProperty("c", String::class.java).should.be.equal("d")
            binded.a().should.be.equal("overrideb$lastReload")
            binded.c().should.be.equal("d")
        } else {
            provider.getProperty("a", String::class.java).should.be.equal("b$lastReload")
            provider.getProperty("c", String::class.java).should.be.equal("d$lastReload")
            binded.a().should.be.equal("b$lastReload")
            binded.c().should.be.equal("d$lastReload")
        }
        lastReload++
    }
    file.delete()
}

interface Nested {
    fun a(): String
}

interface Normal {
    fun nested(): Nested
    fun a(): String
    fun c(): String
}