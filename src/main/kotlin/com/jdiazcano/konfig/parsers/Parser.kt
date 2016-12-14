package com.jdiazcano.konfig.parsers

/**
 * Base Parser interface, not all the implementations will use all the parameters but they will be there in case they
 * are needed.
 */
interface Parser<out T> {
    fun parse(value: String, type: Class<*> = Any::class.java, parser: Parser<*> = StringParser): T
}