package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.asProperties
import com.jdiazcano.cfg4k.loaders.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class PropertiesConfigObjectTest : Spek({
    describe("a properties loader") {
        it("should load correctly") {
            val configObject = javaClass.getResource("/nestedtest.properties").asProperties().toConfig()
            configObject.isObject().should.be.`true`
            val nested = configObject.asObject()["nested"]!!
            nested.isObject().should.be.`true`
            nested.asObject()["a"].should.be.equal("b".toConfig())
        }
    }
})
