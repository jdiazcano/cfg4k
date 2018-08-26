package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.utils.TypeStructure
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

object FileParser : Parser<File> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = File(value.asString())
}

object PathParser : Parser<Path> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = Paths.get(value.asString())
}

object URIParser : Parser<URI> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = URI(value.asString())
}

object URLParser : Parser<URL> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = URL(value.asString())
}