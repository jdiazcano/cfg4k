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

package com.jdiazcano.konfig.parsers

/**
 * Base Parser interface, not all the implementations will use all the parameters but they will be there in case they
 * are needed.
 */
interface Parser<out T> {

    /**
     * Parses a string into a Type
     *
     * @param value The string that comes from the source
     * @param type The class of the supertype of the class that we want to parse
     * @param parser When it is a list, then a parser is passed to parse the items of the list/set/inside
     */
    fun parse(value: String, type: Class<*> = Any::class.java, parser: Parser<*>? = null): T
}