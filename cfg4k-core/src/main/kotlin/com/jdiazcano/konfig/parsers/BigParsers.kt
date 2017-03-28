package com.jdiazcano.konfig.parsers

import java.math.BigDecimal
import java.math.BigInteger

object BigIntegerParser : Parser<BigInteger> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = BigInteger(value)
}

object BigDecimalParser : Parser<BigDecimal> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = BigDecimal(value)
}