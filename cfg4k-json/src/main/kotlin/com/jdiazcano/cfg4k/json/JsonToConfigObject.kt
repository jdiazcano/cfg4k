package com.jdiazcano.cfg4k.json

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.ListConfigObject
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.StringConfigObject
import java.io.InputStream

/**
 * Parse a json string as a ConfigObject
 */
fun Parser.asConfigObjectFromJson(input: InputStream): ConfigObject {
    val parsed = parse(input)
    return when (parsed) {
        is JsonObject -> parseObject(parsed)
        is JsonArray<*> -> parseArray(parsed)
        else -> throw UnsupportedOperationException("Input must be array or object.")
    }
}

private fun parseObject(parsed: JsonObject): ConfigObject {
    return MapConfigObject(parsed.map { (key, value) ->
        when (value) {
            is JsonArray<*> -> key to parseArray(value)
            is JsonObject -> key to parseObject(value)
            else -> key to StringConfigObject(value.toString())
        }
    }.toMap(hashMapOf()))
}

private fun parseArray(array: JsonArray<*>): ConfigObject {
    return ListConfigObject(array.map { item ->
        when (item) {
            is JsonObject -> parseObject(item)
            else -> StringConfigObject(item.toString())
        }
    })
}
