package com.jdiazcano.cfg4k.sources

import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.StringSpec
import java.io.File
import java.nio.file.Paths

class FileConfigSourceTest: StringSpec({

    "the stream from the source should not be null with a file" {
        val source = FileConfigSource(File("src/test/resources/test.properties"))
        source.read().shouldNotBeNull()
    }

    "the stream from the source should not be null with a path" {
        val source = FileConfigSource(Paths.get("src/test/resources/test.properties"))
        source.read().shouldNotBeNull()
    }

})