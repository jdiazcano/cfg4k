package com.jdiazcano.konfig.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GenericType<T> : Typable {
    private val type: Class<T>

    init {
        if (GenericType::class.java != javaClass.superclass) {
            throw IllegalArgumentException("Classes must extend GenericType")
        }

        val parameterizedType = javaClass.genericSuperclass

        if (parameterizedType is ParameterizedType) {
            type = parameterizedType.actualTypeArguments[0] as Class<T>
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