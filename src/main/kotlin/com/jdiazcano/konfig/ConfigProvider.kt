package com.jdiazcano.konfig

import com.jdiazcano.konfig.utils.Typable

interface ConfigProvider {
    fun <T> getProperty(name: String, type: Class<T>): T
    fun <T> getProperty(name: String, type: Typable): T
    fun <T> bind(prefix: String, type: Class<T>): T
    fun canParse(type: Class<out Any>): Boolean
}