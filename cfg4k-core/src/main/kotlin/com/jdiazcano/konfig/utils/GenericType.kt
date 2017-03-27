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

package com.jdiazcano.konfig.utils

import kotlin.reflect.KType

abstract class GenericType<T> : Typable {
    private val type: KType

    init {
        val parameterizedType = javaClass.kotlin.supertypes

        if (parameterizedType.isNotEmpty()) {
            type = javaClass.kotlin.supertypes[0] // this is not working yet but soon(tm)!!
        } else {
            throw IllegalArgumentException("Class must be parameterized")
        }
    }

    override fun getType() = type

    override fun toString(): String {
        return "GenericType { type: $type }"
    }

}

interface Typable {
    fun getType(): KType
}

inline fun <reified T> typeOf() = object : GenericType<T>() {}.getType()