package com.jdiazcano.konfig.parsers

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

object BigIntegerParser : Parser<BigInteger> {
    override fun parse(value: String, type: KClass<*>, parser: Parser<*>) = BigInteger(value)
}

object BigDecimalParser : Parser<BigDecimal> {
    override fun parse(value: String, type: KClass<*>, parser: Parser<*>) = BigDecimal(value)
}