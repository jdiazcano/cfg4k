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

package com.jdiazcano.konfig.parsers

import com.jdiazcano.konfig.utils.ParserClassNotFound
import java.math.BigDecimal
import java.math.BigInteger

object Parsers {
    private val parsers: MutableMap<Class<out Any>, Parser<Any>>
    private val parseredParsers: MutableMap<Class<out Any>, Parser<Any>>

    init {
        parsers = mutableMapOf(
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
                java.lang.Integer::class.java to IntParser,
                java.lang.Long::class.java to LongParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Short::class.java to ShortParser,
                java.lang.Float::class.java to FloatParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Byte::class.java to ByteParser,
                java.lang.String::class.java to StringParser,
                java.lang.Boolean::class.java to BooleanParser,
                Enum::class.java to EnumParser<Nothing>()
        )

        parseredParsers = mutableMapOf(
                List::class.java to ListParser<Nothing>(),
                java.util.List::class.java to ListParser<Nothing>(),
                Set::class.java to SetParser<Nothing>()
        )
    }

    //fun Class<*>.isParser() = this in parsers

    fun isParser(type: Class<*>) = type in parsers

    fun canParse(type: Class<out Any>): Boolean {
        return type in parsers || type in parseredParsers || (type.superclass != null && type.superclass in parsers)
    }

    fun <T> getParser(type: Class<T>): Parser<T> {
        return parsers[type] as Parser<T>
    }

    fun <T> findParser(type: Class<*>?): Parser<T>? {
        if (type == null) {
            return null
        }

        if (type in parsers) {
            return parsers[type] as Parser<T>
        } else if (type.superclass != null && type.superclass in parsers) {
            return parsers[type.superclass!!] as Parser<T>
        } else if (type in parseredParsers) {
            return parseredParsers[type] as Parser<T>
        }

        return throw ParserClassNotFound("Parser not found for class $type")
    }

    fun addParser(type: Class<out Any>, parser: Parser<Any>) {
        parsers.put(type, parser)
    }

}

