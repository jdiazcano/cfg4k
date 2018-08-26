package com.jdiazcano.cfg4k.binders

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.parsers.ListParser
import com.jdiazcano.cfg4k.parsers.MapParser
import com.jdiazcano.cfg4k.parsers.Parsers.findParser
import com.jdiazcano.cfg4k.parsers.Parsers.isParseable
import com.jdiazcano.cfg4k.utils.ParserClassNotFound
import com.jdiazcano.cfg4k.utils.TypeStructure


fun convert(context: ConfigContext, configObject: ConfigObject, structure: TypeStructure): Any? {
    return convertBase(context, configObject, structure) {
        context.bind(context.propertyName, structure.raw)
    }
}

fun convertGet(context: ConfigContext, configObject: ConfigObject, structure: TypeStructure): Any? {
    return convertBase(context, configObject, structure) {
        context.get(context.propertyName, structure.raw)
    }
}

fun convertGetOrNull(context: ConfigContext, configObject: ConfigObject, structure: TypeStructure): Any? {
    return convertBase(context, configObject, structure) {
        context.getOrNull(context.propertyName, structure.raw)
    }
}

fun convertBase(context: ConfigContext, configObject: ConfigObject, structure: TypeStructure, getter: () -> Any?): Any? {
    return when {
        structure.isMap() -> MapParser.parse(context, configObject, structure)
        configObject.isList() -> ListParser.parse(context, configObject, structure)
        structure.raw.isParseable() -> structure.raw.findParser().parse(context, configObject, structure)
        structure.raw.isInterface -> getter()
        else -> throw ParserClassNotFound("Couldn't parse class ${structure.raw}")
    }
}