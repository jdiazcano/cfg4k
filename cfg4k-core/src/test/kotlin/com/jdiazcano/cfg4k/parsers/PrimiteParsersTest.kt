package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk

class PrimiteParsersTest: StringSpec({
    val context = mockk<ConfigContext>()

    "IntParser parses properly" {
        IntParser.parse(context, "1".toConfig()).shouldBe(1)
    }

    "LongParser parses properly" {
        LongParser.parse(context, "1".toConfig()).shouldBe(1L)
    }

    "ShortParser parses properly" {
        ShortParser.parse(context, "1".toConfig()).shouldBe(1.toShort())
    }

    "BooleanParser parses properly" {
        BooleanParser.parse(context, "true".toConfig()).shouldBeTrue()
    }

    "FloatParser parses properly" {
        FloatParser.parse(context, "1.1".toConfig()).shouldBe(1.1F)
    }

    "DoubleParser parses properly" {
        DoubleParser.parse(context, "12.11".toConfig()).shouldBe(12.11)
    }

    "ByteParser parses properly" {
        ByteParser.parse(context, "13".toConfig()).shouldBe(13.toByte())
    }

    "StringParser parses properly" {
        StringParser.parse(context, "asdf".toConfig()).shouldBe("asdf")
    }

    "ClassParser parses properly" {
        ClassParser.parse(context, "io.kotlintest.specs.StringSpec".toConfig()).shouldBe(StringSpec::class.java)
    }

})