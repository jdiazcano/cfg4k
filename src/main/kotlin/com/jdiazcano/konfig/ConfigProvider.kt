package com.jdiazcano.konfig

import com.jdiazcano.konfig.utils.Typable

interface ConfigProvider {
    fun <T: Any> getProperty(name: String, type: Class<T>): T
    fun <T: Any> getProperty(name: String, type: Typable): T
    fun <T: Any> bind(prefix: String, type: Class<T>): T
    fun canParse(type: Class<out Any>): Boolean
    fun reload()
}