package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.utils.asLines
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigProviderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(
                javaClass.classLoader.getResourceAsStream("test.properties").asLines())
        val provider = DefaultConfigProvider(loader)
        it("integer properties") {
            provider.getProperty("integerProperty", Int::class.java).should.be.equal(1)
            provider.getProperty("integerProperty", Integer::class.java).should.be.equal(Integer(1))
        }

        it("long properties") {
            provider.getProperty("longProperty", Long::class.java).should.be.equal(2)
        }

        it("short properties") {
            provider.getProperty("shortProperty", Short::class.java).should.be.equal(1)
        }

        it("float properties") {
            provider.getProperty("floatProperty", Float::class.java).should.be.equal(2.1F)
        }

        it("double properties") {
            provider.getProperty("doubleProperty", Double::class.java).should.be.equal(1.1)
        }

        it("byte properties") {
            provider.getProperty("byteProperty", Byte::class.java).should.be.equal(2)
        }

        it("boolean properties") {
            provider.getProperty("booleanProperty", Boolean::class.java).should.be.`true`
        }

        it("binding test") {
            val testBinder = provider.bind("", TestBinder::class.java)
            testBinder.booleanProperty().should.be.`true`
            testBinder.integerProperty().should.be.equal(1)
            testBinder.longProperty().should.be.equal(2)
            testBinder.shortProperty().should.be.equal(1)
            testBinder.floatProperty().should.be.equal(2.1F)
            testBinder.doubleProperty().should.be.equal(1.1)
            testBinder.byteProperty().should.be.equal(2)
            testBinder.a().should.be.equal("b")
            testBinder.c().should.be.equal("d")
            testBinder.list().should.be.equal(listOf(1, 2, 3))
            testBinder.floatList().should.be.equal(listOf(1.2F, 2.2F, 3.2F))
        }
    }
})

interface TestBinder {
    fun integerProperty(): Int
    fun a(): String
    fun c(): String
    fun booleanProperty(): Boolean
    fun longProperty(): Long
    fun shortProperty(): Short
    fun doubleProperty(): Double
    fun floatProperty(): Float
    fun byteProperty(): Byte
    fun list(): List<Int>
    fun floatList(): List<Float>
}

