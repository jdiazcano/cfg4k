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

        binders.forEach {
            if (it.canBind(method)) {
                return it.bind(method, prefix, provider)
            }
        }

        throw IllegalArgumentException("Can't bind/resolve the property ${method.name}")
    }

}