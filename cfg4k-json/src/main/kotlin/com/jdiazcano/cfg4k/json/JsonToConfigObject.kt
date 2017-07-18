package com.jdiazcano.cfg4k.json

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.jdiazcano.cfg4k.core.ConfigObject

/**
 * Parse a json string as a ConfigObject
 */
fun Parser.asConfigObjectFromJson(json: String): ConfigObject {
    val parsed = parse(StringBuilder(json)) as JsonObject
    return parseObject(parsed)
}

private fun parseObject(parsed: JsonObject): ConfigObject {
    return ConfigObject(parsed.map { (key, value) ->
        when (value) {
            is JsonArray<*> -> key to parseArray(value)
            is JsonObject -> key to parseObject(value)
            else -> key to ConfigObject(value.toString())
        }
    }.toMap(hashMapOf<String, ConfigObject>()))
}

private fun parseArray(array: JsonArray<*>): ConfigObject {
    return ConfigObject(array.map { item ->
        when (item) {
            is JsonObject -> parseObject(item)
            else -> ConfigObject(item.toString())
        }
    })
}
