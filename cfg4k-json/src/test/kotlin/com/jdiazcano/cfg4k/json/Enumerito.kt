package com.jdiazcano.cfg4k.json

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.nio.file.Path

interface TestBinder {
    fun integerProperty(): Int
    fun a(): String
    fun c(): String
    fun booleanProperty(): Boolean
    fun longProperty(): Long
    fun shortProperty(): Short
    fun doubleProperty(): Double
    fun floatProperty(): Float
    fun byteProperty(): Byte
    fun list(): List<Int>
    fun floatList(): List<Float>
    fun complexSet(): Set<Doge>
    fun bigIntegerProperty(): BigInteger
    fun bigDecimalProperty(): BigDecimal
    fun uri(): URI
    fun url(): URL
    fun file(): File
    fun path(): Path
}

enum class TestEnum {
    TEST, TEST1, TEST2
}

interface BindedEnum {
    fun thisWillBeEnum(): TestEnum
}

interface PrefixedBindedEnum {
    fun enumtest(): TestEnum
}

interface Binded {
    fun list(): List<Int>
    fun set(): Set<Int>
    fun enumerito(): List<Enumerito>
}

enum class Enumerito {
    A, B, C
}

interface Doge {
    fun wow(): String
    fun doge(): Int
}

interface NestedBinder {
    fun nested(): TestBinder
    fun normal(): Int
}

interface SuperNested {
    fun supernested(): NestedBinder
    fun normal(): Int
}