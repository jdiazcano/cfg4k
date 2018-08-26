package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.binders.convert
import com.jdiazcano.cfg4k.binders.createCollection
import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.utils.TypeStructure
import java.net.InetAddress
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.util.UUID
import java.util.regex.Pattern

object PatternParser : Parser<Pattern> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = Pattern.compile(value.asString())
}

object RegexParser : Parser<Regex> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = value.asString().toRegex()
}

object UUIDParser : Parser<UUID> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = UUID.fromString(value.asString())
}

object SQLDriverParser : Parser<Driver> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = DriverManager.getDriver(value.asString())
}

object SQLConnectionParser : Parser<Connection> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = DriverManager.getConnection(value.asString())
}

object InetAddressParser : Parser<InetAddress> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure) = InetAddress.getByName(value.asString())
}

object ListParser : Parser<MutableCollection<*>> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure): MutableCollection<*> {
        val collection = createCollection(typeStructure.raw)
        value.asList().forEachIndexed { index, innerObject ->
            collection.add(convert(context.copy(propertyName = "${context.propertyName}[$index]"), innerObject, typeStructure.generics[0]))
        }
        return collection
    }
}

object MapParser {
    fun parse(configContext: ConfigContext, value: ConfigObject, typeStructure: TypeStructure): Map<*, *> {
        return value.asObject().map {
            convert(configContext, it.key.toConfig(), typeStructure.generics[0]) to
                    convert(configContext, it.value, typeStructure.generics[1])
        }.toMap()
    }

}