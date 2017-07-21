package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ConfigObject

abstract class DefaultConfigLoader: ConfigLoader {
    lateinit var root: ConfigObject

    override fun get(key: String): ConfigObject? {
        val split = key.split('.')
        val last = split.last()

        if (split.size == 1) {
            val (number, cleanKey) = findNumbers(last)

            if (number == null) {
                return root.asObject()[last]
            } else {
                return root.asObject()[cleanKey]?.asList()?.get(number)
            }
        } else {
            var root: ConfigObject? = root
            for (index in 0..split.size-2) {
                if (root == null) {
                    return null
                } else if (root.isPrimitive()) {
                    throw IllegalArgumentException("Trying to get a key from a primitive/array")
                }

                val (number, cleanKey) = findNumbers(split[index])

                if (number == null) {
                    root = root.asObject()[split[index]]
                } else {
                    root = root.asObject()[cleanKey]?.asList()?.get(number)
                }
            }
            val (number, cleanKey) = findNumbers(last)

            if (number == null) {
                return root?.asObject()?.get(last)
            } else {
                return root?.asObject()?.get(cleanKey)?.asList()?.get(number)
            }
        }
    }

    private fun findNumbers(key: String): Pair<Int?, String> {
        if (!key.matches("^\\d+.*".toRegex())) {
            return Pair(null, key)
        } else {
            val split = key.split("(?<=\\d)(?=[a-zA-Z])".toRegex(), limit = 2)
            return Pair(split[0].toInt(), split[1])
        }
    }

}