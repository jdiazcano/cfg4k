package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig

fun DefaultConfigLoader(map: Map<String, Any>) = DefaultConfigLoader(map.toConfig())

fun DefaultConfigLoader(vararg pairs: Pair<String, Any>) = DefaultConfigLoader(mapOf(*pairs).toConfig())

open class DefaultConfigLoader(protected var root: ConfigObject = "".toConfig()) : ConfigLoader {
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
            for (index in 0..split.size - 2) {
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

val numberRegex = "([^\\[]+)(?:\\[(\\d+)])".toRegex()

internal fun findNumbers(key: String): Pair<Int?, String> {
    val result = numberRegex.find(key)
    if (result != null) {
        return Pair(result.groups[2]?.value?.toInt(), result.groups[1]?.value!!)
    } else {
        return Pair(null, key)
    }
}