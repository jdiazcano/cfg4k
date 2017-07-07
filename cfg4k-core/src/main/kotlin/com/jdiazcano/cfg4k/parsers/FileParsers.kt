package com.jdiazcano.cfg4k.parsers

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

class FileParser : Parser<File> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = File(value)
}

class PathParser : Parser<Path> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = Paths.get(value)
}

class URIParser : Parser<URI> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = URI(value)
}

class URLParser : Parser<URL> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?) = URL(value)
}