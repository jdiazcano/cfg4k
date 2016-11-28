package com.jdiazcano.konfig.parsers

class EnumParser<T : Enum<T>> : Parser<T> {
    override fun parse(value: String, clazz: Class<T>): T {
        return java.lang.Enum.valueOf(clazz, value)
    }
}