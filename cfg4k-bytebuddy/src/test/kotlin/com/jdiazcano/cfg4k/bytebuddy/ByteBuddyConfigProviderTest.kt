package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ByteBuddyConfigProviderTest : Spek({
    describe("a bytebuddyconfigprovider should ") {
        val provider = ByteBuddyConfigProvider(
                PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("test.properties")))
        )

        it("integer properties") {
            provider.get("integerProperty", Int::class.java).should.be.equal(1)
        }

        it("long properties") {
            provider.get<Long>("longProperty").should.be.equal(2)
        }

        it("short properties") {
            provider.get("shortProperty", Short::class.java).should.be.equal(1)
        }

        it("float properties") {
            provider.get("floatProperty", Float::class.java).should.be.equal(2.1F)
        }

        it("double properties") {
            provider.get("doubleProperty", Double::class.java).should.be.equal(1.1)
        }

        it("byte properties") {
            provider.get("byteProperty", Byte::class.java).should.be.equal(2)
        }

        it("boolean properties") {
            provider.get("booleanProperty", Boolean::class.java).should.be.`true`
        }

        it("binding test") {
            val testBinder = provider.bind<TestBinder>("")
            testBinder.nullProperty().should.be.`null`
            testBinder.booleanProperty().should.be.`true`
            testBinder.integerProperty().should.be.equal(1)
            testBinder.longProperty().should.be.equal(2)
            testBinder.shortProperty().should.be.equal(1)
            testBinder.floatProperty().should.be.equal(2.1F)
            testBinder.doubleProperty().should.be.equal(1.1)
            testBinder.byteProperty().should.be.equal(2)
            testBinder.a().should.be.equal("b")
            testBinder.c().should.be.equal("d")
        }

        it("foreach binding test to MANUALLY detect if there are memory leaks") {
            (1..10).forEach {
                val testBinder = provider.bind<TestBinder>("")
                val otherTestBinder = provider.bind<com.jdiazcano.cfg4k.bytebuddy.subpackage.TestBinder>("")
                testBinder.should.not.be.equals(otherTestBinder) // they must be different instances since classes are different
                testBinder.nullProperty().should.be.`null`
            }
        }

    }
})