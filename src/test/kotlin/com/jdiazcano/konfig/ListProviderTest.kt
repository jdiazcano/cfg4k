package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.utils.typeOf
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ListProviderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("listtest.properties"))
        val provider = DefaultConfigProvider(loader)

        it("Simple property test") {
            val testBinder: List<Int> = provider.getProperty("list", typeOf<List<Int>>())
            testBinder.should.be.equal(listOf(1, 2, 3, 4, 5, 6, 7))
        }

        it("prefixed binding test") {
            val testBinder = provider.bind("prefixed", Binded::class.java)
            testBinder.list().should.be.equal(listOf(1, 2, 3, 4, 5, 6, 7))
            testBinder.set().should.be.equal(setOf(1, 2, 3, 4, 5, 6, 7))
        }

    }
})

interface Binded {
    fun list(): List<Int>
    fun set(): Set<Int>
}