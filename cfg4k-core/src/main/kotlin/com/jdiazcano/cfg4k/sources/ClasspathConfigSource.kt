package com.jdiazcano.cfg4k.sources

import java.io.InputStream

class ClasspathConfigSource(private val resource: String): ConfigSource {
    override fun read(): InputStream {
        return ClasspathConfigSource::class.java.getResourceAsStream(resource)
    }
}