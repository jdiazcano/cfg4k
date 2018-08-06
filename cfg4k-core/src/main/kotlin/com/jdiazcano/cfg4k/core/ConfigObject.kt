package com.jdiazcano.cfg4k.core

import com.jdiazcano.cfg4k.loaders.findNumbers
import java.io.InvalidObjectException

interface ConfigObject {
    val type: ConfigObjectType
    val value: Any

    fun merge(configObject: ConfigObject): ConfigObject

    fun child(key: String): ConfigObject? {
        val (number, cleanKey) = findNumbers(key)

        return when {
            // We're in a normal object
            number == null -> asObject()[cleanKey]
            // We're in a list that is ROOT
            cleanKey.isEmpty() -> asList()[number]
            // When we are in a list that is inside our object
            else -> asObject()[cleanKey]?.asList()?.get(number)
        }
    }

    fun isString() = type == ConfigObjectType.STRING
    fun isObject() = type == ConfigObjectType.OBJECT
    fun isList() = type == ConfigObjectType.LIST
    fun asString() = value.toString()
    fun asObject() = value as Map<String, ConfigObject>
    fun asList() = value as List<ConfigObject>
}

abstract class AbstractConfigObject(
        override val value: Any,
        override val type: ConfigObjectType
) : ConfigObject {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "ConfigObject(value=$value)"
    }
}

class ListConfigObject(value: Collection<ConfigObject>) : AbstractConfigObject(value.toList(), ConfigObjectType.LIST) {
    override fun merge(configObject: ConfigObject): ConfigObject {
        return ListConfigObject((value as Iterable<ConfigObject>).union(configObject.value as Iterable<ConfigObject>))
    }
}

class MapConfigObject(value: Map<String, ConfigObject?>) : AbstractConfigObject(value.toMap(), ConfigObjectType.OBJECT) {
    override fun merge(configObject: ConfigObject): ConfigObject {
        val thisMap = value as Map<String, ConfigObject>
        val thatMap = configObject.value as Map<String, ConfigObject>

        val theList = thisMap.keys.toMutableList()
        val commonKeysMergedMap = hashMapOf<String, ConfigObject>()
        val commonKeys = theList.retainAll(thatMap.keys)

        // Check for primitive common keys
        if (commonKeys) {
            theList.forEach {
                commonKeysMergedMap[it] = thisMap[it]!!.merge(thatMap[it]!!)
            }
        }

        return MapConfigObject(thisMap.plus(thatMap).plus(commonKeysMergedMap))
    }
}

class StringConfigObject(value: String) : AbstractConfigObject(value, ConfigObjectType.STRING) {
    override fun merge(configObject: ConfigObject): ConfigObject {
        throw InvalidObjectException("Can't merge primitive (string) values")
    }
}

enum class ConfigObjectType {
    STRING,
    LIST,
    OBJECT
}

// Add converters for primitive types
fun String.toConfig() = StringConfigObject(this)

fun Int.toConfig() = StringConfigObject(this.toString())
fun Long.toConfig() = StringConfigObject(this.toString())
fun Double.toConfig() = StringConfigObject(this.toString())
fun Float.toConfig() = StringConfigObject(this.toString())
fun Map<String, Any>.toConfig() = parseObject(this)
fun List<*>.toConfig() = parseArray(this)

private fun parseObject(parsed: Map<*, *>): ConfigObject {
    return MapConfigObject(parsed.map { (key, value) ->
        when (value) {
            is List<*> -> key.toString() to parseArray(value)
            is Map<*, *> -> key.toString() to parseObject(value)
            is ConfigObject -> key.toString() to value
            else -> key.toString() to StringConfigObject(value.toString())
        }
    }.toMap(hashMapOf()))
}

private fun parseArray(array: List<*>): ConfigObject {
    return ListConfigObject(array.map { item ->
        when (item) {
            is Map<*, *> -> parseObject(item)
            is ConfigObject -> item
            else -> StringConfigObject(item.toString())
        }
    })
}