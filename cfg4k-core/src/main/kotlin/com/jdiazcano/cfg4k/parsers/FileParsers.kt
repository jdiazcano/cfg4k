package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.asString
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

class FileParser : Parser<File> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = File(value.asString())
}

class PathParser : Parser<Path> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = Paths.get(value.asString())
}

class URIParser : Parser<URI> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = URI(value.asString())
}

class URLParser : Parser<URL> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = URL(value.asString())
}