package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.EnvironmentConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.math.BigDecimal
import java.math.BigInteger

class BigParsersTest: StringSpec({
    val emptyContext = ConfigContext(DefaultConfigProvider(EnvironmentConfigLoader()), "")

    "parsing a big integer" {
        BigIntegerParser.parse(emptyContext, "111111".toConfig()).shouldBe(BigInteger("111111"))
    }

    "parsing a big decimal" {
        BigDecimalParser.parse(emptyContext, "111111".toConfig()).shouldBe(BigDecimal("111111"))
    }
})