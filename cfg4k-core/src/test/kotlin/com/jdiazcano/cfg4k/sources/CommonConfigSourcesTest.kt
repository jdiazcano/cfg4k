package com.jdiazcano.cfg4k.sources

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

class CommonConfigSourcesTest: FeatureSpec({
    feature("StringConfigSource") {
        val source = StringConfigSource("a=1")
        scenario("the stream and the string are the same") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=1")
        }
    }

    feature("StringRotationConfigSource") {
        val source = StringRotationConfigSource(listOf(
                "a=1",
                "a=2"
        ))

        scenario("the first string is good") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=1")
        }

        scenario("second string too!") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=2")
        }

        scenario("it goes back to the first when overflow") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=1")
        }
    }

    feature("FunctionConfigSource") {
        val source = FunctionConfigSource { "a=1".toByteArray() }

        scenario("the content is the same") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=1")
        }
    }

    feature("StringFunctionConfigSource") {
        val source = StringFunctionConfigSource { "a=1" }

        scenario("the content is the same") {
            source.read().readBytes().toString(Charsets.UTF_8).shouldBe("a=1")
        }
    }
})