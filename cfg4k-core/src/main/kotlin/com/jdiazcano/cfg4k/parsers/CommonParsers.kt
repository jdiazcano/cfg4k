package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigObject
import java.net.InetAddress
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.util.UUID
import java.util.regex.Pattern

object PatternParser : Parser<Pattern> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = Pattern.compile(value.asString())
}

object RegexParser : Parser<Regex> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toRegex()
}

object UUIDParser : Parser<UUID> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = UUID.fromString(value.asString())
}

object SQLDriverParser : Parser<Driver> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = DriverManager.getDriver(value.asString())
}

object SQLConnectionParser : Parser<Connection> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = DriverManager.getConnection(value.asString())
}

object InetAddressParser : Parser<InetAddress> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = InetAddress.getByName(value.asString())
}