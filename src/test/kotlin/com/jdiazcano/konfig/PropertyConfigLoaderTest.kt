package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.utils.asLines
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class PropertyConfigLoaderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(
                javaClass.classLoader.getResourceAsStream("test.properties").asLines())
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
        }
    }
})