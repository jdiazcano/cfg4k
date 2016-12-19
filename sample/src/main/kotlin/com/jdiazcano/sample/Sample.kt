package com.jdiazcano.sample

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(DatabaseConfig::class.java.getResource("/global.properties"))
    val provider = DefaultConfigProvider(loader)
    val databaseConfig = provider.bind("database", DatabaseConfig::class.java)

    println("Name: ${databaseConfig.name()}")
    println("Url: ${databaseConfig.url()}")
    println("Port: ${databaseConfig.port()}")
}

interface DatabaseConfig {
    fun url(): String
    fun port(): Int
    fun name(): String
}