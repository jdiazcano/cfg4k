package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigProviderTest: Spek({

    val providers = listOf(
            DefaultConfigProvider(PropertyConfigLoader(javaClass.classLoader.getResource("test.properties"))),
            DefaultConfigProvider(JsonConfigLoader(javaClass.classLoader.getResource("test.json"))),
            CachedConfigProvider(DefaultConfigProvider(PropertyConfigLoader(javaClass.classLoader.getResource("test.properties")))),
            CachedConfigProvider(DefaultConfigProvider(JsonConfigLoader(javaClass.classLoader.getResource("test.json"))))
    )

    providers.forEachIndexed { i, provider ->
        describe("provider[$i]") {
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

                // toString should be the object tostring not the one that comes from the property
                testBinder.toString().should.not.be.equal("this should not be ever used")
            }
        }
    }
})
