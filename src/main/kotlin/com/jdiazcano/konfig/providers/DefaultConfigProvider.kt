package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.parsers.*
import com.jdiazcano.konfig.utils.ParserClassNotFound
import java.lang.reflect.Proxy

@Suppress("UNCHECKED_CAST")
class DefaultConfigProvider(
        val configLoader: ConfigLoader
): ConfigProvider {
    override fun <T> bind(prefix: String, clazz: Class<T>): T {
        val handler = BindingInvocationHandler(this, prefix)
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), handler) as T
    }

    private val parsers: Map<Class<out Any>, Parser<out Any>>
    private val classedParsers: Map<Class<out Any>, Parser<out Any>>

    init {
        parsers = mapOf(
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

        classedParsers = mapOf(
                Enum::class.java to EnumParser<Nothing>()
        )
    }

    override fun <T> getProperty(name: String, type: Class<T>): T {
        if (classedParsers.contains(type.superclass)) {
            val parser = classedParsers[type.superclass!!] as Parser<T>
            return parser.parse(configLoader.get(name), type)
        } else if (parsers.containsKey(type)) {
            return getParser(type).parse(configLoader.get(name))
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun canParse(type: Class<out Any>): Boolean {
        return parsers.containsKey(type) || classedParsers.containsKey(type.superclass!!)
    }

    private fun <T> getParser(type: Class<T>): Parser<T> {
        return parsers[type] as Parser<T>
    }
}