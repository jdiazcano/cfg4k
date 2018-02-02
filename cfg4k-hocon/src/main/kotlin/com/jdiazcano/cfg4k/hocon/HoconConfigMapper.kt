package com.jdiazcano.cfg4k.hocon

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.ListConfigObject
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.StringConfigObject
import com.typesafe.config.Config
import com.typesafe.config.ConfigList

fun Config.toConfig(): ConfigObject {
    return parseObject(root())
}

private fun parseObject(parsed: com.typesafe.config.ConfigObject): ConfigObject {
    return MapConfigObject(parsed.map { (key, value) ->
        when (value) {
            is ConfigList -> key to parseArray(value)
            is com.typesafe.config.ConfigObject -> key to parseObject(value)
            else -> key to StringConfigObject(value.unwrapped().toString())
        }
    }.toMap(hashMapOf()))
}

private fun parseArray(array: ConfigList): ConfigObject {
    return ListConfigObject(array.map { item ->
        when (item) {
            is com.typesafe.config.ConfigObject -> parseObject(item)
            else -> StringConfigObject(item.unwrapped().toString())
        }
    })
}