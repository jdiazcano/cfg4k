package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.StringConfigSource
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ByteBuddyConfigProviderTest : Spek({
    describe("a bytebuddyconfigprovider") {
        val provider = ByteBuddyConfigProvider(
                JsonConfigLoader(URLConfigSource(javaClass.classLoader.getResource("test.json")))
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

        describe("a binded interface") {
            val testBinder = provider.bind<TestBinder>("")

            it("binding test") {

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
                testBinder.listOfLists.should.be.equal(listOf(listOf(1, 2), listOf(3, 4)))
            }

            it("can retrieve a map") {
                testBinder.myCoolMap.should.be.equal(mapOf("one" to 1, "two" to 2))
            }

            it("can retrieve a map with maps and lists") {
                testBinder.myCoolComplexMap.should.be.equal(mapOf(
                        "one" to mapOf(1 to listOf("one", "uno"), 10 to listOf("diez", "ten")),
                        "two" to mapOf(2 to listOf("dos", "two"), 20 to listOf("veinte", "twenty"))))
            }
        }

        it("foreach binding test to MANUALLY detect if there are memory leaks") {
            (1..10).forEach {
                val testBinder = provider.bind<TestBinder>("")
                val otherTestBinder = provider.bind<com.jdiazcano.cfg4k.bytebuddy.subpackage.TestBinder>("")
                testBinder.equals(otherTestBinder).should.be.`false` // they must be different instances since classes are different
                testBinder.nullProperty().should.be.`null`
            }
        }

    }

    describe("a simple bytebuddy provider") {
        val provider = ByteBuddyConfigProvider(PropertyConfigLoader(StringConfigSource("""
            a=b
            nested.a=b
            """)))
        val obj = "b".toConfig()
        val nestedObj = mapOf("a" to "b").toConfig()

        it("has the correct toString") {
            provider.load("a").toString().should.be.equal("ConfigObject(value=b)")
        }

        it("a primitive is equal to the expected ConfigObject") {
            provider.load("a").should.be.equal(obj)
        }

        it("a binding is equal to the expected ConfigObject") {
            provider.bind<Nested>("nested").toString().should.be.equal(nestedObj.toString())
        }
    }
})
