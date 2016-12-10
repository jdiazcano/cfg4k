package com.jdiazcano.konfig

import com.jdiazcano.konfig.utils.Reloadable

interface ConfigLoader : Reloadable {
    fun get(key: String): String
}
