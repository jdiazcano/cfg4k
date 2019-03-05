[![Build Status](https://travis-ci.org/jdiazcano/cfg4k.svg?branch=master)](https://travis-ci.org/jdiazcano/cfg4k) [![Coverage Status](https://coveralls.io/repos/github/jdiazcano/cfg4k/badge.svg?branch=master)](https://coveralls.io/github/jdiazcano/cfg4k?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [ ![Download](https://api.bintray.com/packages/jdiazcano/cfg4k/cfg4k-core/images/download.svg) ](https://bintray.com/jdiazcano/cfg4k/)

# Overview 
Cfg4k is a configuration library made for Kotlin in Kotlin!

Features
* Automatic reload
* Interface binding
* Ability to handle data classes automatically
* All the complex types and generics are supported
* Huge flexibility, custom sources
* Easy to use
* Bytebuddy provider will be able to compile your bindings at runtime (You will need to add the cfg4k-bytebuddy to your dependencies.)

For further information, use the [wiki](https://github.com/jdiazcano/cfg4k/wiki)

# Quick start
1. Add the Bintray repository: 
```groovy
repositories {
    jcenter()
}
```

2. Add the dependency for the module(s) that you are going to use
```
compile 'com.jdiazcano.cfg4k:cfg4k-core:$VERSION'
```

```kotlin
fun main(args: Array<String>) {
    val source = ClassPathConfigSource("global.properties")         // Create source
    val loader = PropertyConfigLoader(source)                       // Create loader
    val provider = ProxyConfigProvider(loader)                      // Create provider
    val databaseConfig = provider.bind<DatabaseConfig>("database")  // bind and use

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
    val unused: String

    @Deprecated("You can even deprecate properties!")
    fun deprecated(): Boolean
    
    val youCanuseValuesToo: String
    val andNullables: Int?
}
```

# Architeture overview

![Lightbox](https://raw.githubusercontent.com/jdiazcano/cfg4k/master/cfg4k-schema.png)

# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
