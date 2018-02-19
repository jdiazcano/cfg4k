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

package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.binders.Binder
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.CachedConfigProvider
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.OverrideConfigProvider
import com.jdiazcano.cfg4k.providers.ProxyConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class TimedReloadStrategyTest : Spek({
    val text = """a=%reload1
c=%reload2
nested.a=reloaded nestedb
"""
    describe("a timed reloadable properties config loader") {
        it("defaultconfigprovider test") {
            val file = File("timedreloadedfile.properties")
            file.createNewFile()
            file.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            file.deleteOnExit()
            val provider = ProxyConfigProvider(PropertyConfigLoader(FileConfigSource(file)), TimedReloadStrategy(1, TimeUnit.SECONDS))
            checkProvider(file, provider, text)
        }

        it("cacheddefaultconfigprovider test") {
            val cachedfile = File("cachedtimedreloadedfile.properties")
            cachedfile.createNewFile()
            cachedfile.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            cachedfile.deleteOnExit()
            val cachedProvider = CachedConfigProvider(ProxyConfigProvider(PropertyConfigLoader(FileConfigSource(cachedfile)), TimedReloadStrategy(1, TimeUnit.SECONDS)))
            checkProvider(cachedfile, cachedProvider, text)
        }

        it("overrideconfigprovider test") {
            val overrideFile = File("qqqqq.properties")
            overrideFile.createNewFile()
            overrideFile.writeText(text.replace("%reload1", "overrideb").replace("c=%reload2\n", ""))
            overrideFile.deleteOnExit()

            val normalFile = File("normal.properties")
            normalFile.createNewFile()
            normalFile.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            normalFile.deleteOnExit()
            val provider = OverrideConfigProvider(
                    DefaultConfigProvider(
                            PropertyConfigLoader(FileConfigSource(overrideFile)),
                            TimedReloadStrategy(1, TimeUnit.SECONDS)
                    ),
                    DefaultConfigProvider(
                            PropertyConfigLoader(FileConfigSource(normalFile)),
                            TimedReloadStrategy(1, TimeUnit.SECONDS)
                    )
            )
            checkProvider(overrideFile, provider, text, true)
        }

        it("error on reload doesn't prevent further reload attempts") {
            var reloadCounter = 0
            val provider = reloadTestProvider {
                // Every second reload throws an exception
                if (reloadCounter++ % 2 == 0) throw Exception("simulate reload failure")
            }
            val reloadStrategy = TimedReloadStrategy(100, TimeUnit.MILLISECONDS)

            reloadStrategy.register(provider)
            Thread.sleep(1000)
            reloadStrategy.deregister(provider)

            // Expected value is low enough to avoid test flakiness
            reloadCounter.should.be.least(5)
        }

        it("reload strategy can be reused by multiple providers") {
            var reloadCounter1 = 0
            val provider1 = reloadTestProvider { reloadCounter1 ++ }
            var reloadCounter2 = 0
            val provider2 = reloadTestProvider { reloadCounter2 ++ }
            val reloadStrategy = TimedReloadStrategy(10, TimeUnit.MILLISECONDS)

            reloadStrategy.register(provider1)
            reloadStrategy.register(provider2)
            Thread.sleep(100)
            reloadStrategy.deregister(provider1)
            val lastSeenReloadCounter1 = reloadCounter1
            val lastSeenReloadCounter2 = reloadCounter2
            Thread.sleep(100)
            reloadStrategy.deregister(provider2)
            val finishedReloadCounter2 = reloadCounter2

            // provider1 must've been unregistered just before lastSeenReloadCounter1 was observed,
            // and at most one running reload might've increased reloadCounter1 since then
            reloadCounter1.should.be.most(lastSeenReloadCounter1 + 1)
            // provider2 has not been unregistered, so its counter must've been running
            reloadCounter2.should.be.least(lastSeenReloadCounter2 + 2)
        }
    }
})

private fun checkProvider(file: File, provider: ConfigProvider, text: String, overriden: Boolean = false) {
    val binded = provider.bind<Normal>("")
    if (overriden) {
        provider.get("a", String::class.java).should.be.equal("overrideb")
        binded.a().should.be.equal("overrideb")
    } else {
        provider.get("a", String::class.java).should.be.equal("b")
        binded.a().should.be.equal("b")
    }
    provider.get("c", String::class.java).should.be.equal("d")
    binded.c().should.be.equal("d")
    var lastReload = 1
    val lastIteration = 3
    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration (8-1)
        }
        if (overriden) {
            file.writeText(text.replace("%reload1", "overrideb$lastReload").replace("c=%reload2\n", ""))
        } else {
            file.writeText(text.replace("%reload1", "b$lastReload").replace("%reload2", "d$lastReload"))
        }
        Thread.sleep(1500)
        if (overriden) {
            provider.get("a", String::class.java).should.be.equal("overrideb$lastReload")
            provider.get("c", String::class.java).should.be.equal("d")
            binded.a().should.be.equal("overrideb$lastReload")
            binded.c().should.be.equal("d")
        } else {
            provider.get("a", String::class.java).should.be.equal("b$lastReload")
            provider.get("c", String::class.java).should.be.equal("d$lastReload")
            binded.a().should.be.equal("b$lastReload")
            binded.c().should.be.equal("d$lastReload")
        }
        lastReload++
    }
}

/**
 *  Creates a no-op [ConfigProvider] with configurable action on [ConfigProvider.reload].
 */
private fun reloadTestProvider(onReload: () -> Unit): ConfigProvider = object : ConfigProvider {
    override fun reload() {
        onReload()
    }

    override val binder: Binder
        get() = throw UnsupportedOperationException("not used in the test")

    override fun <T : Any> get(name: String, type: Class<T>, default: T?): T =
            throw UnsupportedOperationException("not used in the test")

    override fun <T : Any> get(name: String, type: Type, default: T?): T =
            throw UnsupportedOperationException("not used in the test")

    override fun <T> getOrNull(name: String, type: Class<T>, default: T?): T? =
            throw UnsupportedOperationException("not used in the test")

    override fun <T> getOrNull(name: String, type: Type, default: T?): T? =
            throw UnsupportedOperationException("not used in the test")

    override fun load(name: String): ConfigObject? =
            throw UnsupportedOperationException("not used in the test")

    override fun cancelReload(): Unit? =
            throw UnsupportedOperationException("not used in the test")

    override fun addReloadListener(listener: () -> Unit): Unit =
            throw UnsupportedOperationException("not used in the test")

    override fun addReloadErrorListener(listener: (Exception) -> Unit): Unit =
            throw UnsupportedOperationException("not used in the test")

    override fun contains(name: String): Boolean =
            throw UnsupportedOperationException("not used in the test")
}

interface Nested {
    fun a(): String
}

interface Normal {
    fun nested(): Nested
    fun a(): String
    fun c(): String
}
