package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.*
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

class NullsConfigProviderTest: Spek({

    val loader = PropertyConfigLoader(javaClass.classLoader.getResource("nulltest.properties"))
    val providers = listOf(
            Providers.bytebuddy(loader),
            Providers.bytebuddy(loader).cache()
    )
    val overridingLoader = PropertyConfigLoader(javaClass.classLoader.getResource("overridingnulltest.properties"))
    val overridingProvider = OverrideConfigProvider(DefaultConfigProvider(overridingLoader), providers[0])

    providers.forEachIndexed { i, provider ->
        describe("provider[$i]") {
            it("default values") {
                assertFailsWith<SettingNotFound> {
                    provider.bind<ValueNonNullableWithoutDefault>("valueNonNullableWithoutDefault").integerProperty
                }

                provider.bind<ValueNonNullableWithDefault>("valueNonNullable").integerProperty.should.be.equal(1)
                provider.bind<ValueNullableWithDefault>("valueNullableWithDefault").doesntExist.should.be.equal(123456)
                provider.bind<ValueNullableWithoutDefault>("valueNullableWithoutDefault").doesntExist.should.be.`null`

                provider.get<Int>("valueNonNullable.integerProperty").should.be.equal(1)
                provider.get("doesntExist1", 123456).should.be.equal(123456)
                provider.getOrNull<Int?>("doesntExist2").should.be.`null`
                provider.getOrNull<Int>("doesntExist3").should.be.`null`
                provider.getOrNull<Int?>("doesntExist4", 123456).should.be.equal(123456)
                provider.getOrNull("doesntExist5", 123456).should.be.equal(123456)
            }
        }
    }

    describe("an overrided config provider") {
        it("the overrided values should be ok") {
            assertFailsWith<SettingNotFound> {
                overridingProvider.bind<ValueNonNullableWithoutDefault>("valueNonNullableWithoutDefault").integerProperty
            }

            overridingProvider.bind<ValueNonNullableWithDefault>("valueNonNullable").integerProperty.should.be.equal(2)
            overridingProvider.bind<ValueNullableWithDefault>("valueNullableWithDefault").doesntExist.should.be.equal(123456)
            overridingProvider.bind<ValueNullableWithoutDefault>("valueNullableWithoutDefault").doesntExist.should.be.`null`

            overridingProvider.get<Int>("valueNonNullable.integerProperty").should.be.equal(2)
            overridingProvider.get("doesntExist1", 123456).should.be.equal(123456)
            overridingProvider.getOrNull<Int?>("doesntExist2").should.be.`null`
            overridingProvider.getOrNull<Int>("doesntExist3").should.be.`null`
            overridingProvider.getOrNull<Int?>("doesntExist4", 123456).should.be.equal(123456)
            overridingProvider.getOrNull("doesntExist5", 123456).should.be.equal(123456)
        }
    }

    describe("test the loader") {
        it ("should have cool properties") {
            loader.get("valueNonNullable.integerProperty").should.be.equal("1".toConfig())
            loader.get("doesntExist2").should.be.`null`
        }
    }
})

interface ValueNonNullableWithDefault {
    val integerProperty: Int get() = 123456
}

interface ValueNonNullableWithoutDefault {
    val integerProperty: Int
}

interface ValueNullableWithDefault {
    val doesntExist: Int? get() = 123456
}

interface ValueNullableWithoutDefault {
    val doesntExist: Int?
}
