package com.jdiazcano.konfig.parsers

@Suppress("UNCHECKED_CAST")
class ArrayParser<T : List<Any>>: Parser<T> {
    override fun parse(value: String, parser: Parser<*>): T {
        return value.split(',').map {
            parser.parse(it.trim())
        } as T
    }
}