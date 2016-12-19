package com.jdiazcano.konfig

import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.utils.Typable

interface ConfigProvider {
    fun <T: Any> getProperty(name: String, type: Class<T>): T
    fun <T: Any> getProperty(name: String, type: Typable): T
    fun <T: Any> bind(prefix: String, type: Class<T>): T
    fun canParse(type: Class<out Any>): Boolean
    fun reload()

    // These methods must be included because the possibility of registering custom parsers must be there
    fun addParser(type: Class<out Any>, parser: Parser<Any>)
    fun addClassedParser(type: Class<out Any>, parser: Parser<Any>)
    fun addParseredParser(type: Class<out Any>, parser: Parser<Any>)
    fun cancelReload(): Unit?

    fun addReloadListener(listener: () -> Unit)
}