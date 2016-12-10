package com.jdiazcano.konfig.utils

import java.net.URL
import java.util.*

fun URL.asLines(): MutableList<String> {
    val scanner = Scanner(openStream(), "UTF-8")
    val strings = mutableListOf<String>().apply {
        while (scanner.hasNextLine()) {
            add(scanner.nextLine())
        }
    }
    scanner.close()
    return strings
}