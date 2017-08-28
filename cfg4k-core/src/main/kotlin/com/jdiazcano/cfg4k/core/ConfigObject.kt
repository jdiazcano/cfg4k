package com.jdiazcano.cfg4k.core

import com.jdiazcano.cfg4k.loaders.findNumbers

class ConfigObject(value: Any) {
    var value: Any

    // Possible types: String
    // Possible types: List<ConfigObject>
    // Possible types: Map<String, ConfigObject>

    init {
        this.value = when (value) {
            is List<*> -> value
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
        return 31 * value.hashCode()
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

    internal fun child(key: String): ConfigObject? {
        val (number, cleanKey) = findNumbers(key)

        if (number == null) {
            return asObject()[cleanKey]
        } else {
            return asObject()[cleanKey]?.asList()?.get(number)
        }
    }
}

// Add converters for primitive types
fun String.toConfig() = ConfigObject(this)

fun Int.toConfig() = ConfigObject(this.toString())
fun Long.toConfig() = ConfigObject(this.toString())
fun Double.toConfig() = ConfigObject(this.toString())
fun Float.toConfig() = ConfigObject(this.toString())
fun Map<String, Any>.toConfig() = parseObject(this)
fun List<*>.toConfig() = parseArray(this)

private fun parseObject(parsed: Map<*, *>): ConfigObject {
    return ConfigObject(parsed.map { (key, value) ->
        when (value) {
            is List<*> -> key to parseArray(value)
            is Map<*, *> -> key to parseObject(value)
            is ConfigObject -> key to value
            else -> key to ConfigObject(value.toString())
        }
    }.toMap(hashMapOf()))
}

private fun parseArray(array: List<*>): ConfigObject {
    return ConfigObject(array.map { item ->
        when (item) {
            is Map<*, *> -> parseObject(item)
            is ConfigObject -> item
            else -> ConfigObject(item.toString())
        }
    })
}