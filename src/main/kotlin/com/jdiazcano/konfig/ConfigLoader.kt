package com.jdiazcano.konfig

interface ConfigLoader {
    fun get(key: String): String
}
