package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.utils.TypeStructure
import java.math.BigDecimal
import java.math.BigInteger

object BigIntegerParser : Parser<BigInteger> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = BigInteger(value.asString())
}

object BigDecimalParser : Parser<BigDecimal> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = BigDecimal(value.asString())
}