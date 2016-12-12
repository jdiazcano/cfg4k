@file:Suppress("UNCHECKED_CAST")

package com.jdiazcano.konfig.parsers

class ListParser<T : List<Any>>: Parser<T> {
    override fun parse(value: String, parser: Parser<*>): T {
        return value.split(',').map { parser.parse(it.trim()) } as T
    }
}

class SetParser<T : Set<Any>>: Parser<T> {
    override fun parse(value: String, parser: Parser<*>): T {
        return value.split(',').map { parser.parse(it.trim()) }.toSet() as T
    }
}
