package com.jdiazcano.konfig.binding

import com.jdiazcano.konfig.ConfigProvider
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class BindingInvocationHandler(
        val provider: ConfigProvider,
        val prefix: String,
        val binders: List<Binder>
): InvocationHandler {

    val objectMethods: List<String>

    init {
        objectMethods = Object::class.java.declaredMethods.map { it.name }
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        if (objectMethods.contains(method.name)) {
            return method.invoke(this, args)
        }

        return provider.getProperty(prefix(prefix, method.name), method.returnType)
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