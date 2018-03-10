package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.CachedConfigProvider
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.OverrideConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy
import com.jdiazcano.cfg4k.sources.FunctionConfigSource
import com.jdiazcano.cfg4k.sources.StringFunctionConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.runner.RunWith
import org.junit.runner.Runner
import java.util.concurrent.TimeUnit

private fun createText(index: Int = 0, overriden: Boolean = false): String {
    return """a=${if (overriden) "override" else ""}b$index
c=d${if (overriden) "" else index.toString()}
nested.a=reloaded nestedb
"""
}

class ByteBuddyConfigProviderReloadTest : Spek({
    val strategy = TimedReloadStrategy(50, TimeUnit.MILLISECONDS)
    val overridenSource = StringFunctionConfigSource({ createText(lastReload, true) })
    val source = FunctionConfigSource({ createText(lastReload, false).toByteArray() })

    describe("a timed reloadable properties config loader") {
        beforeEachTest {
            lastReload = 1
        }

        it("defaultconfigprovider test") {
            val provider = ByteBuddyConfigProvider(
                    PropertyConfigLoader(source),
                    strategy
            )
            checkProvider(provider)
        }

        it("cacheddefaultconfigprovider test") {
            val cachedProvider = CachedConfigProvider(
                    ByteBuddyConfigProvider(PropertyConfigLoader(source), strategy)
            )
            checkProvider(cachedProvider)
        }

        it("overrideconfigprovider test") {
            val provider = OverrideConfigProvider(
                    ByteBuddyConfigProvider(PropertyConfigLoader(overridenSource), strategy),
                    ByteBuddyConfigProvider(PropertyConfigLoader(source), strategy)
            )
            checkProvider(provider, true)
        }
    }
})

private var lastReload = 1
private const val lastIteration = 3

private fun checkProvider(provider: ConfigProvider, overriden: Boolean = false) {
    val binded = provider.bind<Normal>()
    if (overriden) {
        provider.get("a", String::class.java).should.be.equal("overrideb1")
        binded.a().should.be.equal("overrideb1")
        provider.get("c", String::class.java).should.be.equal("d")
        binded.c().should.be.equal("d")
    } else {
        provider.get("a", String::class.java).should.be.equal("b1")
        binded.a().should.be.equal("b1")
        provider.get("c", String::class.java).should.be.equal("d1")
        binded.c().should.be.equal("d1")
    }

    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration
        }
        Thread.sleep(60)
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

interface Nested {
    fun a(): String
}

interface Normal {
    fun nested(): Nested
    fun a(): String
    fun c(): String
}