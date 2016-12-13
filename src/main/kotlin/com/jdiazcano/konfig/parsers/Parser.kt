package com.jdiazcano.konfig.parsers

/**
 * Base Parser class, normally you will only need one of these three methods in order to parse whatever you want to
 * the problem is that you might want to parse just a String to Int, but you might want to parse a String to List or Set
 * or even Enum, and that's where the classes (for generics) and new parsers come handy, I haven't been able to find
 * another cleaner way to do this
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