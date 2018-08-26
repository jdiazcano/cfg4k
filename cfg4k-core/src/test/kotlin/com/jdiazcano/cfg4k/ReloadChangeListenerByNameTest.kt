package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.addChangeListener
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.StringRotationConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ReloadChangeListenerByNameTest : Spek({
    val provider by memoized {
        val source = StringRotationConfigSource(listOf("a=b", "a=c"))
        val loader = PropertyConfigLoader(source)
        DefaultConfigProvider(loader)
    }

    describe("a provider") {
        it("should fire the reload listener on primitives") {
            var called = false
            provider.addChangeListener<String>("a") { _, _ ->
                called = true
            }

            val before = provider.get<String>("a")
            provider.reload()
            val after = provider.get<String>("a")

            called.should.be.`true`
            before.should.be.equal("b")
            after.should.be.equal("c")
        }

        it("should fire the reload listener on bindings") {
            var called = false
            provider.addChangeListener<Tester>("a") { _, _ ->
                called = true
            }

            provider.reload()
            // TODO bindings should be equals: testerBefore.should.be.equal(testerAfter)
            called.should.be.`true`
        }
    }
})

interface Tester {
    val a: String
}