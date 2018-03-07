package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.ListConfigObject
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class MapParserTest : Spek({
    val mapTests = mapOf(
            mapOf("a" to 1).toConfig() to MapConfigObject(mapOf("a" to 1.toConfig())),
            listOf("a", "b").toConfig() to ListConfigObject(listOf("a".toConfig(), "b".toConfig()))
    )
    describe("a map to be parsed") {
        mapTests.forEach { key, value ->
            key.should.be.equal(value)
        }
    }
})