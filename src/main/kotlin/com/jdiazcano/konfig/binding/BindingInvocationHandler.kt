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
            // TODO this must has the "args" parameter or the equals will throw an error
            // The problem right now is that it's not possible with the toString because it throws an exception if it
            // has the args method (toString has no method) but it's counting the "null" args as 1 argument
            return method.invoke(this)
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