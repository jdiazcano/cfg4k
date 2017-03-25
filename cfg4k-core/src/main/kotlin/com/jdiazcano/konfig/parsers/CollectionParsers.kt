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

import kotlin.reflect.KClass

class ListParser<out T : List<Any>>: Parser<T> {
    override fun parse(value: String, type: KClass<*>, parser: Parser<*>): T {
        return toList(parser, type, value) as T
    }

}

class SetParser<out T : Set<Any>>: Parser<T> {
    override fun parse(value: String, type: KClass<*>, parser: Parser<*>): T {
        return toList(parser, type, value).toSet() as T
    }
}

private fun toList(parser: Parser<*>, type: KClass<*>, value: String): List<Any?> {
    return value.split(',').map {
        if (type.java.isEnum) {
            parser.parse(it.trim(), type)
        } else {
            parser.parse(it.trim())
        }
    }
}