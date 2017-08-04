package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig

fun DefaultConfigLoader(map: Map<String, Any>) = DefaultConfigLoader(map.toConfig())

fun DefaultConfigLoader(vararg pairs: Pair<String, Any>) = DefaultConfigLoader(mapOf(*pairs).toConfig())

open class DefaultConfigLoader(var root: ConfigObject = "".toConfig()): ConfigLoader {
    override fun get(key: String): ConfigObject? {
        if (key == "") {
            return root
        }

        val split = key.split('.')
        val last = split.last()

        if (split.size == 1) {
            return root.child(last)
        } else {
            var root: ConfigObject? = root
            for (index in 0..split.size-2) {
                if (root == null) {
                    return null
                } else if (root.isPrimitive()) {
                    throw IllegalArgumentException("Trying to get a key from a primitive")
                }

                root = root.child(split[index])
            }

            return root?.child(last)
        }
    }

    override fun reload() {}


}

internal fun findNumbers(key: String): Pair<Int?, String> {
    if (!key.matches("^\\d+.*".toRegex())) {
        return Pair(null, key)
    } else {
        val split = key.split("(?<=\\d)(?=[a-zA-Z])".toRegex(), limit = 2)
        return Pair(split[0].toInt(), split[1])
    }
}
