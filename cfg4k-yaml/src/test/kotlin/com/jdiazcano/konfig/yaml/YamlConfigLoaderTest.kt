package com.jdiazcano.konfig.yaml

import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.providers.Providers.Companion.proxy
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class YamlConfigLoaderTest: Spek({

    val loaders = listOf(
            YamlConfigLoader(javaClass.classLoader.getResource("test.yml"))
    )

    loaders.forEachIndexed { i, loader ->
        describe("loader[$i]") {
            it("loader properties") {
                loader.get("integerProperty").should.be.equal("1")
                loader.get("longProperty").should.be.equal("2")
                loader.get("shortProperty").should.be.equal("1")
                loader.get("floatProperty").should.be.equal("2.1")
                loader.get("doubleProperty").should.be.equal("1.1")
                loader.get("byteProperty").should.be.equal("2")
                loader.get("booleanProperty").should.be.equal("true")
                loader.get("nested.nesteda").should.be.equal("nestedb")
            }

            it("works with binding") {
                val provider = proxy(loader)

                val testBinder = provider.bind<TestBinder>("")
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
