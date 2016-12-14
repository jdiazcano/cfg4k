package com.jdiazcano.konfig.parsers

object IntParser: Parser<Int> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toInt()
}

object LongParser: Parser<Long> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toLong()
}

object ShortParser: Parser<Short> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toShort()
}

object BooleanParser: Parser<Boolean> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toBoolean()
}

object FloatParser: Parser<Float> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toFloat()
}

object DoubleParser: Parser<Double> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toDouble()
}

object ByteParser: Parser<Byte> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value.toByte()
}

object StringParser: Parser<String> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = value

}