package com.jdiazcano.cfg4k.sources

import java.io.InputStream

interface ConfigSource {
    fun read(): InputStream
}