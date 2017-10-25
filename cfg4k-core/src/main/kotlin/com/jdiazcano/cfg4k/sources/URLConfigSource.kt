package com.jdiazcano.cfg4k.sources

import java.io.InputStream
import java.net.URL

class URLConfigSource(private val url: URL): ConfigSource {
    constructor(file: String): this(URLConfigSource::class.java.getResource(file))

    override fun read(): InputStream {
        return url.openStream()
    }
}