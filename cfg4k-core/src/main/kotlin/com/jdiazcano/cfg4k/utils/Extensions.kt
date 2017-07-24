package com.jdiazcano.cfg4k.utils

//TODO do we want to keep this method? unflattening a map is not something commonly done
fun Map<String, String>.unflatten(splitBy: Char = '-'): MutableMap<String, Any> {
    val map = mutableMapOf<String, Any>()
    map { (key, value) ->
        val keys = key.split(splitBy)

        if (keys.size == 1) {
            if (map[key] != null) {
                throw IllegalArgumentException("$key is defined twice")
            }

            map[key] = value
        } else {
            val valueMap = keys.dropLast(1).fold(map) { m, k ->
                if (m[k] != null && m[k] !is Map<*, *>) {
                    throw IllegalArgumentException("")
                }

                // TODO Think if on how to make arrays. I think it should be discouraged but...
                // Yeah, without rich syntax like hocon, yaml or json it will not be supported
                m.getOrPut(k) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
            valueMap[keys.last()] = value
        }
    }
    return map
}
