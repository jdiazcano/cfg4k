package com.jdiazcano.cfg4k.sources

import java.io.InputStream

interface ConfigSource {
    fun read(): InputStream
}

/**
 * A class to just proxy the InputStream to a ConfigSource
 */
class InputConfigSource(private val inputStream: InputStream): ConfigSource {
    override fun read() = inputStream
}

/**
 * Convert this InputStream to a ConfigSource
 */
fun InputStream.toConfigSource() = InputConfigSource(this)