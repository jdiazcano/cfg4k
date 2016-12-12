package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.Binder
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.loaders.ReloadStrategy
import com.jdiazcano.konfig.parsers.*
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Proxy

@Suppress("UNCHECKED_CAST")
class DefaultConfigProvider(
        private val configLoader: ConfigLoader,
        private val reloadStrategy: ReloadStrategy? = null
): ConfigProvider, Binder {

    private val parsers: MutableMap<Class<out Any>, Parser<out Any>>
    private val classedParsers: MutableMap<Class<out Any>, Parser<out Any>>
    private val parseredParsers: MutableMap<Class<out Any>, Parser<out Any>>

    init {
        parsers = mutableMapOf(
                Int::class.java to IntParser,
                Long::class.java to LongParser,
                Double::class.java to DoubleParser,
                Short::class.java to ShortParser,
                Float::class.java to FloatParser,
                Double::class.java to DoubleParser,
                Byte::class.java to ByteParser,
                String::class.java to StringParser,
                Boolean::class.java to BooleanParser,
                java.lang.Integer::class.java to IntParser,
                java.lang.Long::class.java to LongParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Short::class.java to ShortParser,
                java.lang.Float::class.java to FloatParser,
                java.lang.Double::class.java to DoubleParser,
                java.lang.Byte::class.java to ByteParser,
                java.lang.String::class.java to StringParser,
                java.lang.Boolean::class.java to BooleanParser
        )

        classedParsers = mutableMapOf(
                Enum::class.java to EnumParser<Nothing>()
        )

        parseredParsers = mutableMapOf(
                List::class.java to ListParser<Nothing>(),
                Set::class.java to SetParser<Nothing>()
        )

        reloadStrategy?.register(this)
    }

    override fun <T: Any> getProperty(name: String, type: Class<T>): T {
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

    override fun <T: Any> getProperty(name: String, type: Typable): T {
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

    override fun <T: Any> bind(prefix: String, type: Class<T>): T {
        val handler = getInvocationHandler(prefix)
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }

    override fun getInvocationHandler(prefix: String) = BindingInvocationHandler(this, prefix)

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

    fun addParser(type: Class<out Any>, parser: Parser<out Any>) {
        parsers.putIfAbsent(type, parser)
    }

    fun addClassedParser(type: Class<out Any>, parser: Parser<out Any>) {
        classedParsers.putIfAbsent(type, parser)
    }

    fun addParseredParser(type: Class<out Any>, parser: Parser<out Any>) {
        parseredParsers.putIfAbsent(type, parser)
    }

    fun cancelReload() = reloadStrategy?.deregister(this)

    override fun reload() = configLoader.reload()
}
