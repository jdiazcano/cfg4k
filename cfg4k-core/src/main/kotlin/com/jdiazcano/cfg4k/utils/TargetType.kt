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

package com.jdiazcano.cfg4k.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

fun Type.convert(): TypeStructure {
    val structure = TypeStructure(this)

    val dewildcarded = dewildcard(this) as? ParameterizedType ?: return structure

    structure.generics.addAll(dewildcarded.actualTypeArguments.map { convertType(it) })

    return structure
}

private fun convertType(typeProvided: Type): TypeStructure {
    val type = dewildcard(typeProvided)
    return when (type) {
        is Class<*> -> TypeStructure(type)

        is ParameterizedType -> {
            TypeStructure(type, type.actualTypeArguments.map { convertType(it) }.toMutableList())
        }

        else -> {
            throw UnsupportedOperationException(
                    "That type contains illegal type argument: '$type' [${type.javaClass}]")
        }
    }
}

private fun dewildcard(type: Type) =
        if (type is WildcardType) type.upperBounds[0] else type

data class TypeStructure(
        val type: Type,
        val generics: MutableList<TypeStructure> = arrayListOf()
) {
    val raw = if (type is ParameterizedType) type.rawType as Class<*> else type as Class<*>

    fun isMap() = type is ParameterizedType && type.rawType is Class<*> && (type.rawType as Class<*>).isAssignableFrom(Map::class.java)
}