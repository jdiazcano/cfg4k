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

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

object Parsers {
    private val parsers: MutableMap<KClass<out Any>, Parser<Any>>
    private val classedParsers: MutableMap<KClass<out Any>, Parser<Any>>
    private val parseredParsers: MutableMap<KClass<out Any>, Parser<Any>>

    init {
        parsers = mutableMapOf(
                Int::class to IntParser,
                Long::class to LongParser,
                Double::class to DoubleParser,
                Short::class to ShortParser,
                Float::class to FloatParser,
                Double::class to DoubleParser,
                Byte::class to ByteParser,
                String::class to StringParser,
                Boolean::class to BooleanParser,
                BigDecimal::class to BigDecimalParser,
                BigInteger::class to BigIntegerParser
        )

        classedParsers = mutableMapOf(
                Enum::class to EnumParser<Nothing>()
        )

        parseredParsers = mutableMapOf(
                List::class to ListParser<Nothing>(),
                java.util.List::class to ListParser<Nothing>(),
                Set::class to SetParser<Nothing>()
        )
    }

    //fun KClass<*>.isParser() = this in parsers

    fun isParser(type: KClass<*>) = type in parsers

    fun isParseredParser(type: KClass<*>?) = type in parseredParsers

    fun isClassedParser(type: KClass<*>?) = type in classedParsers

    fun canParse(type: KClass<out Any>): Boolean {
        return type in parsers
                || type in parseredParsers
                || (type.javaObjectType.superclass != null
                    && Reflection.createKotlinClass(type.javaObjectType.superclass) in classedParsers)
    }

    fun <T> getParser(type: KClass<*>): Parser<T> {
        return parsers[type] as Parser<T>
    }

    fun <T> findParser(type: KClass<*>): Parser<T> {
        if (Reflection.createKotlinClass(type.javaObjectType.superclass)!! in classedParsers) {
            return classedParsers[Reflection.createKotlinClass(type.javaObjectType.superclass)] as Parser<T>
        } else {
            return parsers[type] as Parser<T>
        }
    }

    fun getParseredParser(type: KClass<*>) = parseredParsers[type]

    fun getClassedParser(type: KClass<*>) = classedParsers[type]

    fun addParser(type: KClass<out Any>, parser: Parser<Any>) {
        parsers.put(type, parser)
    }

    fun addClassedParser(type: KClass<out Any>, parser: Parser<Any>) {
        classedParsers.put(type, parser)
    }

    fun addParseredParser(type: KClass<out Any>, parser: Parser<Any>) {
        parseredParsers.put(type, parser)
    }
}

