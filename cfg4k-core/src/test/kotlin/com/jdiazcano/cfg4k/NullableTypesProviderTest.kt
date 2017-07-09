package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.getOrNull
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class NullableTypesProviderTest: Spek({
    describe("a nullable property test") {
        val provider = Providers.proxy(PropertyConfigLoader(javaClass.classLoader.getResource("test.properties")))
        val binded = provider.bind<NullTest>("cool")
        provider.getOrNull<String?>("cool.test").should.be.`null`
        binded.test.should.be.`null`
        binded.nottest.should.be.equal("1")

    }
})

interface NullTest {
    val test: String?
    val nottest: String
}