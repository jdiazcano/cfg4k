package com.jdiazcano.konfig.parsers

class EnumParser<T : Enum<T>> : Parser<T> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): T {
        return java.lang.Enum.valueOf(type as Class<T>, value)
    }
}