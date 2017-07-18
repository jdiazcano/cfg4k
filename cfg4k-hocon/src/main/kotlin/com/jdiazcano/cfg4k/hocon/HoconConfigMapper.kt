package com.jdiazcano.cfg4k.hocon

import com.jdiazcano.cfg4k.core.ConfigObject
import com.typesafe.config.Config
import com.typesafe.config.ConfigList

fun Config.asConfigObject(): ConfigObject {
    return parseObject(root())
}

private fun parseObject(parsed: com.typesafe.config.ConfigObject): ConfigObject {
    return ConfigObject(parsed.map { (key, value) ->
        when (value) {
            is ConfigList -> key to parseArray(value)
            is com.typesafe.config.ConfigObject -> key to parseObject(value)
            else -> key to ConfigObject(value.unwrapped().toString())
        }
    }.toMap(hashMapOf<String, ConfigObject>()))
}

private fun parseArray(array: ConfigList): ConfigObject {
    return ConfigObject(array.map { item ->
        when (item) {
            is com.typesafe.config.ConfigObject -> parseObject(item)
            else -> ConfigObject(item.unwrapped().toString())
        }
    })
}