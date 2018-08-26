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
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy
import com.jdiazcano.cfg4k.sources.FunctionConfigSource
import com.jdiazcano.cfg4k.sources.StringFunctionConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

fun createText(index: Int = 0, overriden: Boolean = false): String {
    return """a=${if (overriden) "override" else ""}b$index
c=d${if (overriden) "" else index.toString()}
nested.a=reloaded nestedb
"""
}

class TimedReloadStrategyTest : Spek({

    val strategy = TimedReloadStrategy(50, TimeUnit.MILLISECONDS)
    val overridenSource = StringFunctionConfigSource({ createText(lastReload, true) })
    val source = FunctionConfigSource({ createText(lastReload, false).toByteArray() })

    describe("a timed reloadable properties config loader") {
        beforeEachTest {
            lastReload = 1
        }

        it("defaultconfigprovider test") {
            val provider = ProxyConfigProvider(
                    PropertyConfigLoader(source),
                    strategy
            )
            checkProvider(provider)
        }

        it("cacheddefaultconfigprovider test") {
            val cachedProvider = CachedConfigProvider(
                    ProxyConfigProvider(PropertyConfigLoader(source),
                    strategy)
            )
            checkProvider(cachedProvider)
        }

        it("overrideconfigprovider test") {
            val provider = OverrideConfigProvider(
                    DefaultConfigProvider(PropertyConfigLoader(overridenSource), strategy),
                    DefaultConfigProvider(PropertyConfigLoader(source), strategy)
            )
            checkProvider(provider, true)
        }

        it("reload strategy can be reused by multiple providers") {
            var reloadCounter1 = 0
            val provider1 = reloadTestProvider { reloadCounter1++ }
            var reloadCounter2 = 0
            val provider2 = reloadTestProvider { reloadCounter2++ }
            val reloadStrategy = TimedReloadStrategy(10, TimeUnit.MILLISECONDS)

            reloadStrategy.register(provider1)
            reloadStrategy.register(provider2)
            Thread.sleep(100)
            reloadStrategy.deregister(provider1)
            val lastSeenReloadCounter1 = reloadCounter1
            val lastSeenReloadCounter2 = reloadCounter2
            Thread.sleep(100)
            reloadStrategy.deregister(provider2)

            // provider1 must've been unregistered just before lastSeenReloadCounter1 was observed,
            // and at most one running reload might've increased reloadCounter1 since then
            reloadCounter1.should.be.most(lastSeenReloadCounter1 + 1)
            // provider2 has not been unregistered, so its counter must've been running
            reloadCounter2.should.be.least(lastSeenReloadCounter2 + 2)
        }
    }
})

private var lastReload = 1
private const val lastIteration = 3

private fun checkProvider(provider: ConfigProvider, overriden: Boolean = false) {
    val binded = provider.bind<Normal>()
    if (overriden) {
        provider.get<String>("a").should.be.equal("overrideb1")
        binded.a().should.be.equal("overrideb1")
        provider.get<String>("c").should.be.equal("d")
        binded.c().should.be.equal("d")
    } else {
        provider.get<String>("a").should.be.equal("b1")
        binded.a().should.be.equal("b1")
        provider.get<String>("c").should.be.equal("d1")
        binded.c().should.be.equal("d1")
    }

    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration
        }
        Thread.sleep(60)
        if (overriden) {
            provider.get<String>("a").should.be.equal("overrideb$lastReload")
            provider.get<String>("c").should.be.equal("d")
            binded.a().should.be.equal("overrideb$lastReload")
            binded.c().should.be.equal("d")
        } else {
            provider.get<String>("a").should.be.equal("b$lastReload")
            provider.get<String>("c").should.be.equal("d$lastReload")
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

    override fun <T : Any> get(name: String, type: Type, default: T?): T =
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
