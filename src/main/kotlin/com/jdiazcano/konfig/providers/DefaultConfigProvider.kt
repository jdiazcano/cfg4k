package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.parsers.*
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class DefaultConfigProvider(
        val configLoader: ConfigLoader
): ConfigProvider {

    private val parsers: Map<Class<out Any>, Parser<out Any>>
    private val classedParsers: Map<Class<out Any>, Parser<out Any>>
    private val parseredParsers: Map<Class<out Any>, Parser<out Any>>

    init {
        parsers = mapOf(
                Int::class.java to IntParser,
                Long::class.java to LongParser,
                Double::class.java to DoubleParser,
                Short::class.java to ShortParser,
                Float::class.java to FloatParser,
                Double::class.java to DoubleParser,
                Byte::class.java to ByteParser,
                String::class.java to StringParser,
                java.lang.Integer::class.java to IntParser,
                java.lang.Long::class.java to LongParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Short::class.java to ShortParser,
                java.lang.Float::class.java to FloatParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Byte::class.java to ByteParser,
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
            return parser.parse(configLoader.get(name), findParser(TargetType(type.getType()).getParameterizedClassArguments()[0]) as Parser<T>)
        } else if (rawType.superclass in classedParsers) {
            val parser = classedParsers[rawType.superclass!!] as Parser<T>
            return parser.parse(configLoader.get(name), rawType as Class<T>)
        } else if (rawType in parsers) {
            return getParser(rawType).parse(configLoader.get(name)) as T
        }
        throw ParserClassNotFound("Parser for class $type was not found")
    }

    override fun <T> bind(prefix: String, type: Class<T>): T {
        val handler = BindingInvocationHandler(this, prefix)
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }

    override fun canParse(type: Class<out Any>): Boolean {
        return type in parsers || type in parseredParsers || (type.superclass != null && type.superclass in classedParsers)
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

    fun getProperty(s: String, reify: KClass<List<Int>>): List<Int> {
        return getProperty(s, reify.java)
    }
}
