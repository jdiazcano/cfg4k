package com.jdiazcano.konfig.binding

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Type

class BindingInvocationHandler(
        private val provider: ConfigProvider,
        private val prefix: String
): InvocationHandler {

    private val objectMethods: List<String>

    init {
        objectMethods = Object::class.java.declaredMethods.map { it.name }
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        if (objectMethods.contains(method.name)) {
            return method.invoke(this, *(args?: arrayOf()))
        }

        val type = method.genericReturnType
        if (provider.canParse(method.returnType)) {
            return provider.getProperty(prefix(prefix, method.name), object : Typable {
                override fun getType(): Type = type
            })
        } else {
            return provider.bind(prefix(prefix, method.name), method.returnType)
        }

    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other?.hashCode()
    }

}

fun prefix(before: String, after: String): String {
    return buildString {
        append(before)
        if (before.isNotEmpty()) {
            append('.')
        }
        append(after)
    }
}

interface Binder {
    fun getInvocationHandler(prefix: String): BindingInvocationHandler
}