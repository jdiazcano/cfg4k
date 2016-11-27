package com.jdiazcano.konfig.binding

import com.jdiazcano.konfig.ConfigProvider
import java.lang.reflect.Method

interface Binder {
    fun canBind(method: Method): Boolean
    fun bind(method: Method, prefix: String, provider: ConfigProvider)

    fun prefix(before: String, after: String): String {
        return buildString {
            append(before)
            if (before.isNotEmpty()) {
                append('.')
            }
            append(after)
        }
    }
}