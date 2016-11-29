package com.jdiazcano.konfig.parsers

/**
 * Created by javierdiaz on 26/11/2016.
 */
interface Parser<T> {
    fun parse(value: String): T {
        TODO("Not yet implemented or not needed")
    }

    fun parse(value: String, clazz: Class<T>): T {
        TODO("Not yet implemented or not needed")
    }

    fun parse(value: String, parser: Parser<*>): T {
        TODO("Not yet implemented or not needed")
    }
}