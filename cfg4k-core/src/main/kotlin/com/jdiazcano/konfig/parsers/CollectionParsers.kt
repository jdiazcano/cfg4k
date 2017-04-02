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

interface CollectionParser<out T> : Parser<T> {
    val prefix: String
    val suffix: String
    val divider: String
}

// The defaults are from the JSON, I am not sure about YAML or HOCON, I guess it should be handled so the same toString
// should be used inside all the "array" types inside hocon, yaml.
// TODO() Check this
class ListParser<out T : List<Any>>(
        override val prefix: String = "JsonArray(value=[",
        override val suffix: String = "])",
        override val divider: String = ","
): CollectionParser<T> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?): T {
        if (parser == null) {
            throw ParserClassNotFound("Parser class not found for type '${type.name}'")
        } else {
            return toList(parser, type, value) as T
        }
    }

}

class SetParser<out T : Set<Any>>(
        override val prefix: String = "JsonArray(value=[",
        override val suffix: String = "])",
        override val divider: String = ","
): CollectionParser<T> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>?): T {
        if (parser == null) {
            throw ParserClassNotFound("Parser class not found for type '${type.name}'")
        } else {
            return toList(parser, type, value).toSet() as T
        }
    }
}

private fun CollectionParser<*>.toList(parser: Parser<*>, type: Class<*>, value: String): List<Any?> {
    val splitString = value.removePrefix(prefix).removeSuffix(suffix).split(divider)
    return splitString.map { parser.parse(it.trim(), type) }
}
