package com.jdiazcano.konfig.binding

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Type

class BindingInvocationHandler(
        val provider: ConfigProvider,
        val prefix: String
): InvocationHandler {

    val objectMethods: List<String>

    init {
        objectMethods = Object::class.java.declaredMethods.map { it.name }
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        if (objectMethods.contains(method.name)) {
            return method.invoke(this, args)
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

    fun prefix(before: String, after: String): String {
        return buildString {
            append(before)
            if (before.isNotEmpty()) {
                append('.')
            }
            append(after)
        }
    }

}