package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.Binder
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.parsers.*
import java.lang.reflect.Proxy

@Suppress("UNCHECKED_CAST")
class DefaultConfigProvider(
        val configLoader: ConfigLoader,
        val binders: List<Binder> = listOf()
): ConfigProvider {
    override fun <T> bind(prefix: String, clazz: Class<out T>): T {
        val handler = BindingInvocationHandler(this, prefix, binders)
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), handler) as T
    }

    private val parsers: Map<Class<out Any>, Parser<Any>>

    init {
        parsers = mapOf(
                Int::class.java to IntParser,
                Long::class.java to LongParser,
                Double::class.java to DoubleParser,
                Short::class.java to ShortParser,
                Float::class.java to FloatParser,
                Double::class.java to DoubleParser
        )
    }

    override fun <T> getProperty(name: String, type: Class<out T>): T {
        return getParser(type).parse(configLoader.get(name))
    }

    private fun <T> getParser(type: Class<out T>): Parser<T> {
        return parsers[type] as Parser<T>
    }
}