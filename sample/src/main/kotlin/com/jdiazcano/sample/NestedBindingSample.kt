/*
 * Copyright 2015-2016 Javier Díaz-Cano Martín-Albo (javierdiazcanom@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jdiazcano.sample

import com.jdiazcano.konfig.bind
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(GlobalConfig::class.java.getResource("/global.properties")) // Create loader
    val provider = DefaultConfigProvider(loader)                                                  // Create provider
    val globalConfig = provider.bind<GlobalConfig>("")                                            // bind and use

    println("Database name: ${globalConfig.database().name()}")
    println("Web url: ${globalConfig.web().url()}")

    // You can also just get static strings
    println("String Web port: ${provider.getProperty("web.port", String::class.java)}")
    // Or you can have it as Int, the library will parse it for you
    println("Int Web port: ${provider.getProperty("web.port", Int::class.java)}")
}

/**
 * This interface defines a database configuration
 */
interface GlobalConfig {
    fun database(): DatabaseConfig
    fun web(): WebConfig
}

interface WebConfig {
    fun url(): String
    fun port(): Int
}