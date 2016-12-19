package com.jdiazcano.konfig

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

interface NestedBinder {
    fun nested(): TestBinder
    fun normal(): Int
}

interface SuperNested {
    fun supernested(): NestedBinder
    fun normal(): Int
}