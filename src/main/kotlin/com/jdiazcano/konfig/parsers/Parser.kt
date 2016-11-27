package com.jdiazcano.konfig.parsers

/**
 * Created by javierdiaz on 26/11/2016.
 */
interface Parser<out T> {
    fun parse(value: String): T
}