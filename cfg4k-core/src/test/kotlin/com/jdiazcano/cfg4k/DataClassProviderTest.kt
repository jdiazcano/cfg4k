package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.*
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.lang.Exception
import kotlin.test.assertFailsWith

class DataClassProviderTest : Spek({
    val source = URLConfigSource(javaClass.classLoader.getResource("test.properties"))
    val providers = listOf(
            Providers.proxy(PropertyConfigLoader(source)),
            Providers.proxy(PropertyConfigLoader(source)).cache()

    )

    providers.forEach { provider ->
        describe("provider ${provider.javaClass.simpleName}") {
            beforeEachTest {
                provider.reload()
            }

            it("can bind a normal class") {
                val cool = provider.bind<MyCoolDataClass>("cool")
                cool.nottest.should.be.equal(1)
                cool.another.should.be.equal("wow")
            }

            it("can bind a nested class") {
                val cool = provider.bind<ClassBindNested>("nested")
                cool.normal.should.be.equal(1)
                cool.supernested.normal.should.be.equal(2)
            }

            it("can bind a class with nullables with default") {
                val cool = provider.bind<MyCoolDataClassWithNullables>("cool")
                cool.nottest.should.be.equal(1)
                cool.another.should.be.equal("wow")
            }

            it("can bind a class with nullables without default") {
                val cool = provider.bind<MyCoolDataClassWithNullablesWithoutDefault>("cool")
                cool.nottest.should.be.equal(1)
                cool.another.should.be.equal("wow")
            }

            it("can bind a nested class with an interface") {
                val cool = provider.bind<ClassBindNestedButWithInterface>("nested")
                cool.normal.should.be.equal(1)
                cool.supernested.normal().should.be.equal(2)
            }

            it("can not bind a private class") {
                assertFailsWith<IllegalArgumentException> {
                    provider.bind<MyPrivateCoolDataClass>("cool")
                }
            }

            it("exception on no constructor found because the types do not match and it can be whatever") {
                assertFailsWith<Exception> {
                    provider.bind<MyCoolDataClassAnotherConstructorNotFound>("cool")
                }
            }
        }
    }
})

internal data class MyCoolDataClassAnotherConstructorNotFound(
        val nottest: Int,
        val another: Int
)

internal data class MyCoolDataClass(
        val nottest: Int,
        val another: String
)

internal data class MyCoolDataClassWithNullables(
        val nottest: Int,
        val another: String,
        val icanbenull: String? = null
)

internal data class MyCoolDataClassWithNullablesWithoutDefault(
        val nottest: Int,
        val another: String,
        val icanbenull: String?
)

private data class MyPrivateCoolDataClass(
        val nottest: Int,
        val another: String
)

internal data class ClassBindNested(
        val normal: Int,
        val supernested: ClassBindSupernested
)

internal data class ClassBindNestedButWithInterface(
        val normal: Int,
        val supernested: NestedBinder
)

internal data class ClassBindSupernested(
        val normal: Int
)