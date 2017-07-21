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

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.utils.ParserClassNotFound


// The defaults are from the JSON, I am not sure about YAML or HOCON, I guess it should be handled so the same toString
// should be used inside all the "array" types inside hocon, yaml.
// TODO() Check this THIS WILL BE MOSTLY GONE WITH THE NEW CONFIGOBJECT
class ListParser<out T>: Parser<List<T>> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): List<T> {
        if (parser == null) {
            throw ParserClassNotFound("Parser class not found for type '${type.name}'")
        } else {
            return value.asList().map {
                parser.parse(it, type)
            } as List<T>
        }
    }

}

class SetParser<out T : Set<Any>>: Parser<T> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): T {
        if (parser == null) {
            throw ParserClassNotFound("Parser class not found for type '${type.name}'")
        } else if (!value.isArray()) {
            throw IllegalArgumentException("This is not an array: $value")
        } else {
            return value.asList().map {
                parser.parse(it, type)
            }.toSet() as T
        }
    }
}