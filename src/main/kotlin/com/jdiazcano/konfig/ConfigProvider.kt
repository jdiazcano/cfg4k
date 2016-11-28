package com.jdiazcano.konfig

interface ConfigProvider {
    fun <T> getProperty(name: String, type: Class<T>): T
    fun <T> bind(prefix: String, clazz: Class<T>): T
    fun canParse(type: Class<out Any>): Boolean
}