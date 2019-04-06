package com.jdiazcano.cfg4k.sources

import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.StringSpec

class ConfigSourceTest: StringSpec({

    "the stream from the source should not be null" {
        val source = ClasspathConfigSource("/test.properties")
        source.read().toConfigSource().read().shouldNotBeNull()
    }

})