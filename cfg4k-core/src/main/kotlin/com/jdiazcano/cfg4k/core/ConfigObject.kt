package com.jdiazcano.cfg4k.core

class ConfigObject {
    val type: ConfigObjectType

    lateinit var value: String
    lateinit var list: List<ConfigObject>
    lateinit var properties: Map<String, ConfigObject>

    constructor(value: String) {
        this.value = value
        this.type = ConfigObjectType.PRIMITIVE
    }

    constructor(list: List<ConfigObject>) {
        this.list = list
        this.type = ConfigObjectType.ARRAY
    }

    constructor(properties: Map<String, ConfigObject>) {
        this.properties = properties
        this.type = ConfigObjectType.OBJECT
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ConfigObject

        if (type != other.type) return false
        return when (type) {
            ConfigObjectType.ARRAY -> list == other.list
            ConfigObjectType.PRIMITIVE -> value == other.value
            ConfigObjectType.OBJECT -> properties == other.properties
        }
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + when (type) {
            ConfigObjectType.ARRAY -> list.hashCode()
            ConfigObjectType.PRIMITIVE -> value.hashCode()
            ConfigObjectType.OBJECT -> properties.hashCode()
        }
        return result
    }

    override fun toString(): String {
        val stringValue = when (type) {
            ConfigObjectType.ARRAY -> "list=$list"
            ConfigObjectType.PRIMITIVE -> "value=$value"
            ConfigObjectType.OBJECT -> "properties=$properties"
        }
        return "ConfigObject(type=$type, $stringValue)"
    }


}

enum class ConfigObjectType {
    PRIMITIVE,
    ARRAY,
    OBJECT,
}

// Add converters for primitive types
fun String.toConfig() = ConfigObject(this)
fun Int.toConfig() = ConfigObject(this.toString())
fun Long.toConfig() = ConfigObject(this.toString())
fun Double.toConfig() = ConfigObject(this.toString())
fun Float.toConfig() = ConfigObject(this.toString())

// TODO do this for lists and maps String, *