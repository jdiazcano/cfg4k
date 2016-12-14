@file:Suppress("UNCHECKED_CAST")

package com.jdiazcano.konfig.parsers

class ListParser<T : List<Any>>: Parser<T> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): T {
        return toList(parser, type, value) as T
    }

}

class SetParser<T : Set<Any>>: Parser<T> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): T {
        return toList(parser, type, value).toSet() as T
    }
}

private fun toList(parser: Parser<*>, type: Class<*>, value: String): List<Any?> {
    return value.split(',').map {
        if (type.isEnum) {
            parser.parse(it.trim(), type)
        } else {
            parser.parse(it.trim())
        }
    }
}
