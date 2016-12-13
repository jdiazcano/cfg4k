package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class NestedBindingTest: Spek({
    describe("nested properties") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("nestedtest.properties"))
        val provider = DefaultConfigProvider(loader)
        it("this is testing a normal nested property, when calling nested then it shall return a binding for it") {
            val bind = provider.bind("", NestedBinder::class.java)
            bind.nested().a().should.be.equal("b")
            bind.normal().should.be.equal(2)
        }
    }

    describe("super nested properties") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("supernestedtest.properties"))
        val provider = DefaultConfigProvider(loader)
        it("deeply nested property") {
            val bind = provider.bind("", SuperNested::class.java)
            bind.supernested().nested().a().should.be.equal("b")
            bind.normal().should.be.equal(3)
        }
    }
})

interface NestedBinder {
    fun nested(): TestBinder
    fun normal(): Int
}

interface SuperNested {
    fun supernested(): NestedBinder
    fun normal(): Int
}