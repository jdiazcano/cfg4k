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

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.ProxyConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(DatabaseConfig::class.java.getResource("/global.properties")) // Create loader
    val provider = ProxyConfigProvider(loader)                                                      // Create provider
    val databaseConfig = provider.bind("database", DatabaseConfig::class.java)                      // bind and use

    println("Name: ${databaseConfig.name()}")
    println("Url: ${databaseConfig.url()}")
    println("Port: ${databaseConfig.port()}")
}

/**
 * This interface defines a database configuration
 */
interface DatabaseConfig {
    /**
     * You can have javadocs inside your properties and this is really cool
     */
    fun url(): String
    fun port(): Int
    fun name(): String

    // if you have an unused property you know it and you can delete it
    fun unused(): String

    @Deprecated("You can even deprecate properties!")
    fun deprecated(): Boolean
}