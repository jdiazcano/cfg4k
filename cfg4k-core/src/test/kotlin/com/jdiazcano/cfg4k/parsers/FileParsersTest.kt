package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Paths

class FileParsersTest: StringSpec({
    val context = mockk<ConfigContext>()

    "FileParser parses properly" {
        FileParser.parse(context, "myfile.txt".toConfig()).shouldBe(File("myfile.txt"))
    }

    "PathParser parses properly" {
        PathParser.parse(context, "mypath/test.txt".toConfig()).shouldBe(Paths.get("mypath/test.txt"))
    }

    "URIParser parses properly" {
        URIParser.parse(context, "https://www.amazon.com".toConfig()).shouldBe(URI("https://www.amazon.com"))
    }

    "URLParser parses properly" {
        URLParser.parse(context, "https://www.amazon.com".toConfig()).shouldBe(URL("https://www.amazon.com"))
    }

})