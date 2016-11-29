package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.parsers.*
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
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
    private val parseredParsers: Map<Class<out Any>, Parser<out Any>>

    init {
        parsers = mapOf(
                Int::class.java to IntParser,
                java.lang.Integer::class.java to IntParser,
                Long::class.java to LongParser,
                java.lang.Long::class.java to LongParser,
                Double::class.java to DoubleParser,
                java.lang.Double::class.java to DoubleParser,
                Short::class.java to ShortParser,
                java.lang.Short::class.java to ShortParser,
                Float::class.java to FloatParser,
                java.lang.Float::class.java to FloatParser,
                Double::class.java to DoubleParser,
                java.lang.Double::class.java to DoubleParser,
                Byte::class.java to ByteParser,
                java.lang.Byte::class.java to ByteParser,
                String::class.java to StringParser,
                java.lang.String::class.java to StringParser
        )

        classedParsers = mapOf(
                Enum::class.java to EnumParser<Nothing>()
        )

        parseredParsers = mapOf(
                List::class.java to ArrayParser<Nothing>()
        )
    }

    override fun <T> getProperty(name: String, type: Class<T>): T {
        if (type in parseredParsers) {
            val parser = parseredParsers[type] as Parser<T>
            return parser.parse(configLoader.get(name), findParser(type))
        } else if (type in classedParsers) {
            val parser = classedParsers[type] as Parser<T>
            return parser.parse(configLoader.get(name), type)
        } else if (parsers.containsKey(type)) {
            return getParser(type).parse(configLoader.get(name))
        } else {
            throw ParserClassNotFound("Parser for class ${type.name} was not found")
        }
    }

    override fun <T> getProperty(name: String, type: Typable): T {
        val rawType = TargetType(type.getType()).rawTargetType()
        if (rawType in parseredParsers) {
            val parser = parseredParsers[rawType] as Parser<T>
            return parser.parse(configLoader.get(name), findParser(TargetType(type.getType()).extractParameterizedClassArguments()[0]) as Parser<T>)
        } else if (rawType in classedParsers) {
            val parser = classedParsers[rawType] as Parser<T>
            return parser.parse(configLoader.get(name), rawType as Class<T>)
        } else if (rawType in parsers) {
            return getParser(rawType).parse(configLoader.get(name)) as T
        } else {
            throw ParserClassNotFound("Parser for class ${type} was not found")
        }
    }

    override fun canParse(type: Class<out Any>): Boolean {
        return type in parsers || type in parseredParsers || classedParsers.containsKey(type.superclass!!)
    }

    private fun <T> getParser(type: Class<T>): Parser<T> {
        return parsers[type] as Parser<T>
    }

    private fun <T> findParser(type: Class<T>): Parser<T> {
        if (type in parseredParsers) {
            return parseredParsers[type] as Parser<T>
        } else if (type.superclass!! in classedParsers) {
            return classedParsers[type.superclass!!] as Parser<T>
        } else {
            return parsers[type] as Parser<T>
        }
    }
}
