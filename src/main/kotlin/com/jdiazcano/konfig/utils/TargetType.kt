package com.jdiazcano.konfig.utils

import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

internal class TargetType(private val targetType: Type) {
    private var rawTargetType: Class<*>? = null
    private var parameterizedClassArguments: List<Class<*>>? = null

    val isTargetTypeParameterized: Boolean
        get() {
            if (targetType is ParameterizedType) {
                return true
            }
            return false
        }

    fun targetType(): Type {
        return targetType
    }

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
            if (typeArgument is Class<*>) {
                result.add(typeArgument)
                continue
            }
            if (typeArgument is ParameterizedType) {
                val rawType = typeArgument.rawType
                if (rawType is Class<*>) {
                    result.add(rawType)
                    continue
                }
            }
            var message = "That type contains illegal type argument: '%s' [%s]"
            message = String.format(message, typeArgument, typeArgument.javaClass)
            throw UnsupportedOperationException(message)
        }
        return result
    }

    override fun toString(): String {
        return targetType.toString()
    }

}