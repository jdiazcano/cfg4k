package com.jdiazcano.konfig.utils

import java.lang.reflect.*
import java.lang.reflect.Array
import java.util.*

internal class TargetType(private val targetType: Type) {
    private var rawTargetType: Class<*>? = null
    private var parameterizedClassArguments: List<Class<*>>? = null

    val isTargetTypeParameterized: Boolean
        get() = targetType is ParameterizedType

    fun targetType() = targetType

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
        if (targetType is GenericArrayType) {
            val componentType = targetType.genericComponentType
            if (componentType is Class<*>) {
                /*
                 * This is a special case that only happens in Java version 1.6
                 * (example: java version "1.6.0_30")
                 */
                return Array.newInstance(componentType, 0).javaClass
            }
            if (componentType is ParameterizedType) {
                val rawType = componentType.rawType as Class<*>
                return Array.newInstance(rawType, 0).javaClass
            }
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