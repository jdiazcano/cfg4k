package com.jdiazcano.cfg4k

data class Potato(val name: String, val size: Int)

data class ObjectWithAllTheThings(
        val intProperty: Int,
        val stringProperty: String,
        val longProperty: Long,
        val shortProperty: Short,
        val byteProperty: Byte,
        val doubleProperty: Double,
        val floatProperty: Float,
        val potato: Potato,
        val potatoList: List<Potato>,
        val potatoMap: Map<String, Potato>,
        val nullableString: String?,
//        val stringWithDefault: String = "def", TODO defaults in data classes aren't totally supported
        val randomThing: String?
)

interface InterfacePropertyWithAllTheThings {
    val intProperty: Int
    val stringProperty: String
    val longProperty: Long
    val shortProperty: Short
    val byteProperty: Byte
    val doubleProperty: Double
    val floatProperty: Float
    val potato: Potato
    val potatoList: List<Potato>
    val potatoMap: Map<String, Potato>
    val randomThing: String?
    val nullableString: String?
}

interface InterfaceMethodWithAllTheThings {
    fun intProperty(): Int
    fun stringProperty(): String
    fun longProperty(): Long
    fun shortProperty(): Short
    fun byteProperty(): Byte
    fun doubleProperty(): Double
    fun floatProperty(): Float
    fun potato(): Potato
    fun potatoList(): List<Potato>
    fun potatoMap(): Map<String, Potato>
    fun randomThing(): String?
    fun nullableString(): String?
    fun stringWithDefault(): String = "def"
}