package com.jdiazcano.cfg4k.providers

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.sources.StringConfigSource
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class OverrideConfigProviderTest: StringSpec({
    val source1 = StringConfigSource("a=1\nb=1\nc=1")
    val source2 = StringConfigSource("a=2\nb=2\nd=2")

    val provider1 = DefaultConfigProvider(PropertyConfigLoader(source1))
    val provider2 = DefaultConfigProvider(PropertyConfigLoader(source2))

    val provider = OverrideConfigProvider(provider1, provider2)

    "the provider is not overriden for 'a'" {
        provider.get<Int>("a") shouldBe 1
    }

    "the provider is not overriden for 'b'" {
        provider.get<Int>("b") shouldBe 1
    }

    "the provider is not overriden for 'c'" {
        provider.get<Int>("c") shouldBe 1
    }

    "the provider is overriden for 'd'" {
        provider.get<Int>("d") shouldBe 2
    }
})