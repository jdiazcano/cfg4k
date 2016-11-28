package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.utils.asLines
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class EnumBindingTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(
                javaClass.classLoader.getResourceAsStream("enumtest.properties").asLines())
        val provider = DefaultConfigProvider(loader)

        it("binding test") {
            val testBinder = provider.bind("", BindedEnum::class.java)
            testBinder.thisWillBeEnum().should.be.equal(TestEnum.TEST)
        }
    }
})

enum class TestEnum {
    TEST, TEST1, TEST2
}

interface BindedEnum {
    fun thisWillBeEnum(): TestEnum
}

