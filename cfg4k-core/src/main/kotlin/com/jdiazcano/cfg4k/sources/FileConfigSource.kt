package com.jdiazcano.cfg4k.sources

import java.io.File
import java.io.InputStream
import java.nio.file.Path

class FileConfigSource(private val file: File): ConfigSource {
    override fun read(): InputStream {
        return file.inputStream()
    }
}

fun FileConfigSource(path: Path) = FileConfigSource(path.toFile())
