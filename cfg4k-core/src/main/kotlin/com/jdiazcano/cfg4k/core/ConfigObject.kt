package com.jdiazcano.cfg4k.core

import com.sun.org.apache.xpath.internal.operations.Bool

class ConfigObject(value: Any) {
    val value: Any

    // Possible types: String
    // Possible types: List<ConfigObject>
    // Possible types: Map<String, ConfigObject>

    init {
        this.value = when (value) {
            is List<*>   -> value
            is Map<*, *> -> value
            else -> value.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ConfigObject

        return value == other.value
    }

    override fun hashCode(): Int {
        var result = 31 * value.hashCode()
        return result
    }

    override fun toString(): String {
        return "ConfigObject(value=$value)"
    }

    fun isObject() = value is Map<*, *>
    fun isArray() = value is List<*>
    fun isPrimitive() = !isObject() && !isArray()

    fun asObject() = value as Map<String, ConfigObject>
    fun asList() = value as List<ConfigObject>
    fun asString() = value.toString()
}

// Add converters for primitive types
fun String.toConfig() = ConfigObject(this)
fun Int.toConfig() = ConfigObject(this.toString())
fun Long.toConfig() = ConfigObject(this.toString())
fun Double.toConfig() = ConfigObject(this.toString())
fun Float.toConfig() = ConfigObject(this.toString())

// TODO do this for lists and maps String, *