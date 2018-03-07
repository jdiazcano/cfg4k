package com.jdiazcano.cfg4k.sources

import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Static String source.
 */
class StringConfigSource(private val string: String): ConfigSource {
    override fun read(): InputStream {
        return ByteArrayInputStream(string.toByteArray())
    }
}

/**
 * Dynamic String source that will rotate through all the listed strings
 */
class StringRotationConfigSource(private val strings: List<String>): ConfigSource {
    private var i = 0

    override fun read(): InputStream {
        return ByteArrayInputStream(strings[i++%strings.size].toByteArray())
    }
}