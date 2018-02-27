package com.jdiazcano.cfg4k.sources

import java.io.ByteArrayInputStream
import java.io.InputStream

class StringConfigSource(private val string: String): ConfigSource {
    override fun read(): InputStream {
        return ByteArrayInputStream(string.toByteArray())
    }

}