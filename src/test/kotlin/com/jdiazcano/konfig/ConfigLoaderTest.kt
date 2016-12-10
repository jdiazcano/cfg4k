package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.utils.asLines
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigLoaderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("test.properties").asLines())
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
        }
    }

    describe("a json config loader") {
        val loader = JsonConfigLoader(javaClass.classLoader.getResource("test.json"))
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
            loader.get("nested.a").should.be.equal("nestedb")
        }
    }
})