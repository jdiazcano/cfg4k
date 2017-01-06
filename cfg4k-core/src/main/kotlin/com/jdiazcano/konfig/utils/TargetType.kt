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

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.*

internal class TargetType(private val targetType: Type) {
    private var rawTargetType: Class<*>? = null
    private var parameterizedClassArguments: List<Class<*>>? = null

    val isTargetTypeParameterized: Boolean
        get() = targetType is ParameterizedType

    fun rawTargetType(): Class<*> {
        if (rawTargetType == null) {
            rawTargetType = extractRawTargetType()
        }
        return rawTargetType!!
    }

    private fun extractRawTargetType(): Class<*> {
        if (targetType is Class<*>) {
            return targetType
        }
        if (isTargetTypeParameterized) {
            val type = targetType as ParameterizedType
            return type.rawType as Class<*>
        }
        return targetType.javaClass
    }

    fun getParameterizedClassArguments(): List<Class<*>> {
        if (parameterizedClassArguments == null) {
            parameterizedClassArguments = extractParameterizedClassArguments()
        }
        return parameterizedClassArguments!!
    }

    fun extractParameterizedClassArguments(): List<Class<*>> {
        if (!isTargetTypeParameterized) {
            throw UnsupportedOperationException("Type $targetType must be parameterized")
        }

        val pt = targetType as ParameterizedType
        val result = ArrayList<Class<*>>()
        for (typeArgument in pt.actualTypeArguments) {
            when (typeArgument) {
                is Class<*> -> result.add(typeArgument)

                is ParameterizedType -> {
                    val rawType = typeArgument.rawType
                    if (rawType is Class<*>) {
                        result.add(rawType)
                    }
                }

                is WildcardType -> {
                    val rawType = typeArgument.upperBounds[0]
                    if (rawType is Class<*>) {
                        result.add(rawType)
                    }
                }

                else -> {
                    throw UnsupportedOperationException(
                            "That type contains illegal type argument: '$typeArgument' [${typeArgument.javaClass}]")
                }
            }
        }
        return result
    }

    override fun toString() = targetType.toString()

}