package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigObjectTest: Spek({
    describe("equality of config objects") {
        it("should be equals as int") {
            val one = 1.toConfig()
            val anotherOne = 1.toConfig()

            one.should.be.equal(anotherOne)
        }

        it("should be equals as string") {
            val one = "thisisbest".toConfig()
            val anotherOne = "thisisbest".toConfig()

            one.should.be.equal(anotherOne)
        }

        it("should be equals as float") {
            val one = 1.2F.toConfig()
            val anotherOne = 1.2F.toConfig()

            one.should.be.equal(anotherOne)
        }

        it("should be equals as double") {
            val one = 1.1.toConfig()
            val anotherOne = 1.1.toConfig()

            one.should.be.equal(anotherOne)
        }

        it("should be equals as long") {
            val one = 1000000000L.toConfig()
            val anotherOne = 1000000000L.toConfig()

            one.should.be.equal(anotherOne)
        }

        it("should be equals as objects") {
            val one = ConfigObject(mapOf(
                    "test" to 1.toConfig(),
                    "str" to "bestest".toConfig()
            ))
            val anotherOne = ConfigObject(mapOf(
                    "test" to 1.toConfig(),
                    "str" to "bestest".toConfig()
            ))

            one.should.be.equal(anotherOne)
        }

        it("should be equals as lists") {
            val one = ConfigObject(listOf(
                    1.toConfig(),
                    "test".toConfig(),
                    ConfigObject(mapOf(
                            "test" to 1.toConfig(),
                            "str" to "bestest".toConfig()
                    ))
            ))
            val anotherOne = ConfigObject(listOf(
                    1.toConfig(),
                    "test".toConfig(),
                    ConfigObject(mapOf(
                            "test" to 1.toConfig(),
                            "str" to "bestest".toConfig()
                    ))
            ))

            one.should.be.equal(anotherOne)
        }
    }
})