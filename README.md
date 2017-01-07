[![Build Status](https://travis-ci.org/jdiazcano/cfg4k.svg?branch=master)](https://travis-ci.org/jdiazcano/cfg4k) [![Coverage Status](https://coveralls.io/repos/github/jdiazcano/cfg4k/badge.svg?branch=master)](https://coveralls.io/github/jdiazcano/cfg4k?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Release](https://jitpack.io/v/jdiazcano/cfg4k.svg)](https://jitpack.io/#jdiazcano/cfg4k)

#Overview 
Cfg4k is a configuration library made for Kotlin in Kotlin!

Features
* Automatic reload
* Interface binding
* Huge flexibility, custom sources
* Easy to use
* Performance matters, the new Bytebuddy provider will be able to compile your bindings at runtime in order to get the same performance as if you were writing the code yourself!

#Quick start
1. Add the JitPack repository: 
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
2. Add the dependency: `com.github.jdiazcano.cfg4k:cfg4k-core:0.2`. 
This is a multimodule dependency so the core will always be needed but you can add other support by adding other modules:
    - `com.github.jdiazcano.cfg4k:cfg4k-bytebuddy:0.2` - Adds bytebuddy Provider instead of a normal Proxy provider, this is needed if you want more performance. Classes are compiled at runtime and then the execution is instant instead of proxying and checking with every call.
3. Write code as in the example

# Dependency explained
The dependency is from JitPack so it has a schema:

1. `com.github.jdiazcano` = github user
2. `cfg4k` = project name
3. `cfg4k-bytebuddy` = module name (folder inside the project)
4. `0.2` = version, here you can write a tag or branch (maybe followed by `-SNAPSHOT`* to get the latest version)

\* Sometimes the SNAPSHOT build takes times in JitPack when building for the first time, be patient if it takes longer than expected. It is only the first time globally!

# Example

```kotlin
import PropertyConfigLoader
import com.jdiazcano.konfig.providers.ProxyConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(DatabaseConfig::class.java.getResource("/global.properties")) // Create loader
    val provider = ProxyConfigProvider(loader)                                                      // Create provider
    val databaseConfigKotlin = provider.bind<DatabaseConfig>("database")                            // bind and use
    val databaseConfigJava   = provider.bind("database", DatabaseConfig::class.java)                // bind and use

    println("Name: ${databaseConfigKotlin.name()}")
    println("Url: ${databaseConfigKotlin.url()}")
    println("Port: ${databaseConfigJava.port()}")
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

\* Have in mind that not everything is supported in code coverage in Kotlin (inline extension functions) so the code coverage might appear worse than it really is!

# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
