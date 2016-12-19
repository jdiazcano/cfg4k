package com.jdiazcano.konfig.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class GenericType<T> : Typable {
    private val type: Type

    init {
        val parameterizedType = javaClass.genericSuperclass

        if (parameterizedType is ParameterizedType) {
            type = parameterizedType.actualTypeArguments[0]
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
    fun getType(): Type
}

inline fun <reified T> typeOf() = object : GenericType<T>() {}