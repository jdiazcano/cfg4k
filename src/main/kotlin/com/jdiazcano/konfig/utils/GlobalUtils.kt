package com.jdiazcano.konfig.utils

import java.io.InputStream
import java.util.*

fun InputStream.asLines(): MutableList<String> {
    val scanner = Scanner(this, "UTF-8")
    val strings = mutableListOf<String>().apply {
        while (scanner.hasNextLine()) {
            add(scanner.nextLine())
        }
    }
    scanner.close()
    return strings
}