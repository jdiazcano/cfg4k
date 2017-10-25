package com.jdiazcano.cfg4k.yaml

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.providers.Providers.proxy
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class YamlConfigLoaderTest : Spek({

    val loaders = listOf(
            YamlConfigLoader(URLConfigSource(javaClass.classLoader.getResource("test.yml")))
    )

    loaders.forEachIndexed { i, loader ->
        describe("loader[$i]") {
            it("loader properties") {
                loader.get("integerProperty").should.be.equal(1.toConfig())
                loader.get("longProperty").should.be.equal(2.toConfig())
                loader.get("shortProperty").should.be.equal(1.toConfig())
                loader.get("floatProperty").should.be.equal(2.1.toConfig())
                loader.get("doubleProperty").should.be.equal(1.1.toConfig())
                loader.get("byteProperty").should.be.equal("2".toConfig())
                loader.get("booleanProperty").should.be.equal("true".toConfig())
                loader.get("nested.nesteda").should.be.equal("nestedb".toConfig())
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

                testBinder.complexList.size.should.be.equal(2)
                testBinder.complexList[0].age().should.be.equal(1)
                testBinder.complexList[1].age().should.be.equal(100)
                testBinder.complexList[0].name.should.be.equal("pepe")
                testBinder.complexList[1].name.should.be.equal("thefrog")

                testBinder.complexSet.size.should.be.equal(2)

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

    val complexList: List<User>
    val complexSet: List<User>
}

interface User {
    val name: String
    fun age(): Int
}
