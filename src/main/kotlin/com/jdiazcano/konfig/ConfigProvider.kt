package com.jdiazcano.konfig

interface ConfigProvider {
    fun <T> getProperty(name: String, type: Class<out T>): T
    fun <T> bind(prefix: String, clazz: Class<out T>): T
}