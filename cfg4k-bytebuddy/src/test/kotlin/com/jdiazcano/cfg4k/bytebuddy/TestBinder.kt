package com.jdiazcano.cfg4k.bytebuddy

interface TestBinder {
    fun nullProperty(): Int?
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
    val listOfLists: List<List<Int>>
    val myCoolMap: Map<String, Int>
    val myCoolComplexMap: Map<String, Map<Int, List<String>>>
}