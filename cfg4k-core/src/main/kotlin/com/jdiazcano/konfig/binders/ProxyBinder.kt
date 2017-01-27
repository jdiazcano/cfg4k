package com.jdiazcano.konfig.binders

import com.jdiazcano.konfig.Binder
import com.jdiazcano.konfig.ConfigProvider
import java.lang.reflect.Proxy

@Suppress("UNCHECKED_CAST")
class ProxyBinder : Binder {
    override fun <T : Any> bind(provider: ConfigProvider, prefix: String, type: Class<T>): T {
        val handler = BindingInvocationHandler(provider, prefix)
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }
}
