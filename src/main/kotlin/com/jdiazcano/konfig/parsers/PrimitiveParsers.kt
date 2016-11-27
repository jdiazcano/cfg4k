package com.jdiazcano.konfig.parsers

object IntParser: Parser<Int> {
    override fun parse(value: String) = value.toInt()
}

object LongParser: Parser<Long> {
    override fun parse(value: String) = value.toLong()
}

object ShortParser: Parser<Short> {
    override fun parse(value: String) = value.toShort()
}

object BooleanParser: Parser<Boolean> {
    override fun parse(value: String) = value.toBoolean()
}

object FloatParser: Parser<Float> {
    override fun parse(value: String) = value.toFloat()
}

object DoubleParser: Parser<Double> {
    override fun parse(value: String) = value.toDouble()
}

object ByteParser: Parser<Byte> {
    override fun parse(value: String) = value.toByte()
}

object StringParser: Parser<String> {
    override fun parse(value: String): String = value

}