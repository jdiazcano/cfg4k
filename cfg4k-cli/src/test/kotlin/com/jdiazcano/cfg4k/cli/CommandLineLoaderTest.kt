package com.jdiazcano.cfg4k.cli

import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class CommandLineLoaderTest : Spek({
    val tests = hashMapOf(
            arrayOf("-test=1", "-number=100", "-whatever=doge") to hashMapOf(
                    "test" to 1.toConfig(),
                    "number" to 100.toConfig(),
                    "whatever" to "doge".toConfig()
            ).toConfig()
    )
     describe("a command line loader") {
         tests.forEach { key, value ->
            val loader = CommandLineLoader(key)
            loader.root.should.be.equal(value)
         }
     }
})