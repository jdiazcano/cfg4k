package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.asString
import java.math.BigDecimal
import java.math.BigInteger

object BigIntegerParser : Parser<BigInteger> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = BigInteger(value.asString())
}

object BigDecimalParser : Parser<BigDecimal> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = BigDecimal(value.asString())
}