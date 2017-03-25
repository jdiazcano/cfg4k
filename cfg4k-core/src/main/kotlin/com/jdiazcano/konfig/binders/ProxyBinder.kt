package com.jdiazcano.konfig.binders

import com.jdiazcano.konfig.providers.ConfigProvider
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ProxyBinder : Binder {
    override fun <T : Any> bind(provider: ConfigProvider, prefix: String, type: KClass<T>): T {
        val handler = BindingInvocationHandler(provider, prefix)
        return Proxy.newProxyInstance(type.java.classLoader, arrayOf(type.java), handler) as T
    }
}
