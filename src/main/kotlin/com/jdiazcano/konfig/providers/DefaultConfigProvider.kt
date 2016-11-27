package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.Binder
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.parsers.*
import com.jdiazcano.konfig.utils.ParserClassNotFound
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

    private val javaParsers: Map<Class<out Any>, Parser<Any>>

    init {
        javaParsers = mapOf(
                Int::class.java to IntParser,
                Integer::class.java to IntParser,
                Long::class.java to LongParser,
                Double::class.java to DoubleParser,
                Short::class.java to ShortParser,
                Float::class.java to FloatParser,
                Double::class.java to DoubleParser,
                Byte::class.java to ByteParser,
                String::class.java to StringParser
        )
    }

    override fun <T> getProperty(name: String, type: Class<out T>): T {
        if (javaParsers.containsKey(type)) {
            return getParser(type).parse(configLoader.get(name))
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun canParse(type: Class<out Any>?): Boolean {
        return javaParsers.containsKey(type)
    }

    private fun <T> getParser(type: Class<out T>): Parser<T> {
        return javaParsers[type] as Parser<T>
    }
}