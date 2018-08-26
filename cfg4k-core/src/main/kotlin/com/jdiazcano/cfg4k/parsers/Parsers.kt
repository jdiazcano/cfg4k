/*
 * Copyright 2015-2016 Javier Díaz-Cano Martín-Albo (javierdiazcanom@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNCHECKED_CAST")

package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.utils.ParserClassNotFound
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.sql.Connection
import java.sql.Driver
import java.util.UUID
import java.util.regex.Pattern

object Parsers {
    private val parsers: MutableMap<Class<out Any>, Parser<Any>> = mutableMapOf(
            Int::class.java to IntParser,
            Long::class.java to LongParser,
            Double::class.java to DoubleParser,
            Short::class.java to ShortParser,
            Float::class.java to FloatParser,
            Double::class.java to DoubleParser,
            Byte::class.java to ByteParser,
            String::class.java to StringParser,
            Boolean::class.java to BooleanParser,
            BigDecimal::class.java to BigDecimalParser,
            BigInteger::class.java to BigIntegerParser,
            Enum::class.java to EnumParser<Nothing>(),
            Class::class.java to ClassParser,
            File::class.java to FileParser,
            Path::class.java to PathParser,
            URL::class.java to URLParser,
            URI::class.java to URIParser,
            Regex::class.java to RegexParser,
            Pattern::class.java to PatternParser,
            UUID::class.java to UUIDParser,
            Driver::class.java to SQLDriverParser,
            Connection::class.java to SQLConnectionParser,
            InetAddress::class.java to InetAddressParser,

            /* These are needed for compatibility */
            java.lang.Integer::class.java to IntParser,
            java.lang.Long::class.java to LongParser,
            java.lang.Double::class.java to DoubleParser,
            java.lang.Short::class.java to ShortParser,
            java.lang.Float::class.java to FloatParser,
            java.lang.Double::class.java to DoubleParser,
            java.lang.Byte::class.java to ByteParser,
            java.lang.String::class.java to StringParser,
            java.lang.Boolean::class.java to BooleanParser
    )

    fun Class<*>.isParseable() = this in parsers || isEnum

    fun Class<*>.isExtendedParseable() = isParseable() || Collection::class.java.isAssignableFrom(this) || Map::class.java.isAssignableFrom(this)

    fun Class<*>.findParser(): Parser<*> {
        if (isEnum) {
            return parsers[Enum::class.java] as Parser<*>
        } else if (isParseable()) {
            return parsers[this] as Parser<*>
        }

        throw ParserClassNotFound("Parser not found for class $this")
    }

    fun addParser(type: Class<out Any>, parser: Parser<Any>) {
        parsers[type] = parser
    }

}

