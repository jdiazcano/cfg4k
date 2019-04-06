package com.jdiazcano.cfg4k.reloadstrategies

import com.jdiazcano.cfg4k.binders.Binder
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.*
import com.jdiazcano.cfg4k.sources.FunctionConfigSource
import com.jdiazcano.cfg4k.sources.StringFunctionConfigSource
import io.kotlintest.TestCase
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

fun createText(index: Int = 0, overriden: Boolean = false): String {
    return """a=${if (overriden) "override" else ""}b$index
c=d${if (overriden) "" else index.toString()}
nested.a=reloaded nestedb
"""
}

class TimedReloadStrategyTest : StringSpec() {

    override fun beforeTest(testCase: TestCase) {
        lastReload = 1
    }

    init {
        val strategy = TimedReloadStrategy(50, TimeUnit.MILLISECONDS)
        val overridenSource = StringFunctionConfigSource { createText(lastReload, true) }
        val source = FunctionConfigSource { createText(lastReload, false).toByteArray() }

        "defaultconfigprovider test" {
            val provider = ProxyConfigProvider(
                    PropertyConfigLoader(source),
                    strategy
            )
            checkProvider(provider)
        }

        "cacheddefaultconfigprovider test" {
            val cachedProvider = CachedConfigProvider(
                    ProxyConfigProvider(PropertyConfigLoader(source),
                            strategy)
            )
            checkProvider(cachedProvider)
        }

        "overrideconfigprovider test" {
            val provider = OverrideConfigProvider(
                    DefaultConfigProvider(PropertyConfigLoader(overridenSource), strategy),
                    DefaultConfigProvider(PropertyConfigLoader(source), strategy)
            )
            checkProvider(provider, true)
        }

        "reload strategy can be reused by multiple providers" {
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
            reloadCounter1.shouldBeLessThan(lastSeenReloadCounter1 + 1)
            // provider2 has not been unregistered, so its counter must've been running
            reloadCounter2.shouldBeGreaterThan(lastSeenReloadCounter2 + 2)
        }
    }
}

private var lastReload = 1
private const val lastIteration = 3

private fun checkProvider(provider: ConfigProvider, overriden: Boolean = false) {
    val binded = provider.bind<Normal>()
    if (overriden) {
        provider.get<String>("a").shouldBe("overrideb1")
        binded.a().shouldBe("overrideb1")
        provider.get<String>("c").shouldBe("d")
        binded.c().shouldBe("d")
    } else {
        provider.get<String>("a").shouldBe("b1")
        binded.a().shouldBe("b1")
        provider.get<String>("c").shouldBe("d1")
        binded.c().shouldBe("d1")
    }

    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration
        }
        Thread.sleep(60)
        if (overriden) {
            provider.get<String>("a").shouldBe("overrideb$lastReload")
            provider.get<String>("c").shouldBe("d")
            binded.a().shouldBe("overrideb$lastReload")
            binded.c().shouldBe("d")
        } else {
            provider.get<String>("a").shouldBe("b$lastReload")
            provider.get<String>("c").shouldBe("d$lastReload")
            binded.a().shouldBe("b$lastReload")
            binded.c().shouldBe("d$lastReload")
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

interface Normal {
    fun a(): String
    fun c(): String
}
