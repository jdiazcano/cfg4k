[![Build Status](https://travis-ci.org/jdiazcano/cfg4k.svg?branch=master)](https://travis-ci.org/jdiazcano/cfg4k) [![Coverage Status](https://coveralls.io/repos/github/jdiazcano/cfg4k/badge.svg?branch=master)](https://coveralls.io/github/jdiazcano/cfg4k?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Release](https://jitpack.io/v/jdiazcano/cfg4k.svg)](https://jitpack.io/#jdiazcano/cfg4k)

#Overview 
Cfg4k is a configuration library made for Kotlin in Kotlin!

Features
* Automatic reload
* Interface binding
* Huge flexibility, custom sources
* Easy to use

#Quick start
1. Add the JitPack repository: 
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
2. Add the dependency: `com.github.jdiazcano:cfg4k:0.1`
3. Write code as in the example

# Example

```kotlin
import PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(DatabaseConfig::class.java.getResource("/global.properties")) // Create loader
    val provider = DefaultConfigProvider(loader)                                                    // Create provider
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
```

Full example inside the sample module: https://github.com/jdiazcano/cfg4k/tree/master/sample

# License
Licensed under the Apache License, Version 2.0. See LICENSE file.