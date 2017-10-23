[![Build Status](https://travis-ci.org/jdiazcano/cfg4k.svg?branch=master)](https://travis-ci.org/jdiazcano/cfg4k) [![Coverage Status](https://coveralls.io/repos/github/jdiazcano/cfg4k/badge.svg?branch=master)](https://coveralls.io/github/jdiazcano/cfg4k?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [ ![Download](https://api.bintray.com/packages/jdiazcano/cfg4k/cfg4k-core/images/download.svg) ](https://bintray.com/jdiazcano/cfg4k/)

# Overview 
Cfg4k is a configuration library made for Kotlin in Kotlin!

Features
* Automatic reload
* Interface binding
* Huge flexibility, custom sources
* Easy to use
* Bytebuddy provider will be able to compile your bindings at runtime

# Overview

![Lightbox](https://raw.githubusercontent.com/jdiazcano/cfg4k/master/cfg4k-schema.svg?sanitize=true)

# Quick start
1. Add the Bintray repository (Requested jcenter sync): 
```groovy
repositories {
    jcenter()
}
```

2. Add the dependency for the module(s) that you are going to use
```
compile 'com.jdiazcano.cfg4k:cfg4k-core:0.7.1'
```

# Example

```kotlin
import PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.ProxyConfigProvider

fun main(args: Array<String>) {
    val loader = PropertyConfigLoader(DatabaseConfig::class.java.getResource("/global.properties")) // Create loader
    val provider = ProxyConfigProvider(loader)                                                      // Create provider
    val databaseConfig = provider.bind<DatabaseConfig>("database")                            // bind and use

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

# Detailed features

## Providers
1. DefaultConfigProvider: Base class for all the other providers. It has the basic functionality for a Provider. (You can obviously ignore it if implementing a new provider)
1. CachedConfigProvider: This provider will cache the calls into a map and use the cached one once the same call is done again. When the method `reload` is called this cache will be cleared.
1. OverrideConfigProvider: With this provider you can input a list of Loaders in order of precedence and it will return the first one that is not null/empty. So you can override properties for example: `EnvironmentConfigLoader -> JsonConfigLoader` it would pick first from Environment and then from Json.

## Binders
1. ProxyBinder: Uses Java `InvocationHandler` in order to implement the interface and intercept the calls to return the correct value.
1. ByteBuddyBinder: Uses `ByteBuddy` to create a class that implements the interface and returns the value.

## Loaders
1. DefaultConfigLoader: This loader can take static map or vararg of pairs to create a loader from it.
1. JsonConfigLoader: Load the properties from a Json file, an URL must be provided.
1. PropertyConfigLoader: Load the properties from a Java properties file.
1. EnvironmentConfigLoader: Load the properties from the Environment Variables. This will transform the environment variables so they can be used inside the project and stay consistent with the rest of the properties. For example having the variable `GLOBAL_URL` will result in the property `global.url`
    1. `_` will become `.`
    1. `-` will become `.`
    1. The property name will become lowercase
1. HoconConfigLoader: Load the properties from a Hocon file. This has many advantages over Json or Property (it is a superset of both) so I recommend this loader but not before taking a look at the project: https://github.com/typesafehub/config#using-hocon-the-json-superset
1. YamlConfigLoader: Load the properties from a Yaml file.
1. GitConfigLoader: Load the properties from a Git repository, possible giving the branch name or folder to denote different environments.

## Reload strategies
1. TimedReloadStrategy: This will reload the properties on a time basis.
2. FileChangeReloadStrategy: This will reload when a file content has changed. This will only look for edits on the file that is passed as argument and this should be the same file than in the ConfigLoader

## Parsers
These parsers are supported out of the box

1. Int
1. Long
1. Double
1. Short
1. Float
1. Double
1. Byte
1. String
1. Boolean
1. List<Any>
1. Set<Any>
1. Enum
1. BigInteger
1. BigDecimal
1. LocalDateTime
1. LocalDate
1. ZonedDateTime
1. OffsetDateTime
1. OffsetTime
1. Calendar
1. Date
1. URI
1. URL
1. File
1. Path
1. Class<*>

# Customizing Cfg4k

## Providers
You can create your own Providers by implementing `ConfigProvider` or extending `DefaultConfigProvider`, keep in mind that the default provider already has the parsing so it is always a good practice to extend it.

Example of how caching properties can be done with the `CachedConfigProvider`

```kotlin
class CachedConfigProvider(val configProvider: ConfigProvider) : ConfigProvider by configProvider {
    private val cache = mutableMapOf<String, Any>()

    init {
        configProvider.addReloadListener { cache.clear() }
    }

    override fun <T : Any> get(name: String, type: Class<T>, default: T?): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property = configProvider.get(name, type, default)
            cache[name] = property
            return property
        }
    }

    override fun <T: Any> get(name: String, type: Typable, default: T?): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property: T = configProvider.get(name, type, default)
            cache[name] = property
            return property
        }
    }

    override fun <T: Any> bind(prefix: String, type: Class<T>): T {
        // This is using %pre. in order to not collide with general properties
        val cachePrefix = "%pre.$prefix"
        if (cache.containsKey(cachePrefix)) {
            return cache[cachePrefix] as T
        } else {
            val property: T = configProvider.bind(prefix, type)
            cache[cachePrefix] = property
            return property
        }
    }
}
```
More examples in the package `providers`

## Loaders
You can create your own config loader by implementing the `ConfigLoader` interface (or extending `DefaultConfigLoader` which provides the base code for "get")
```kotlin
open class SystemPropertyConfigLoader : ConfigLoader {
    override fun reload() {
        // Nothing to do, the System.properties do the reload for us!
    }

    override fun get(key: String): ConfigObject {
        return System.get(key, "").toConfig()
    }

}
```
More examples in the package `loaders`

## Reload strategies
You can create your own reloading strategy by implementing the `ReloadStrategy` interface. Remember that you have to use it then in the provider.

```kotlin
class TimedReloadStrategy(val time: Long, val unit: TimeUnit) : ReloadStrategy {

    private lateinit var reloadTimer: Timer

    override fun register(configProvider: ConfigProvider) {
        reloadTimer = timer("TimeReloadStrategy", true, unit.toMillis(time), unit.toMillis(time)) {
            configProvider.reload()
        }
    }

    override fun deregister(configProvider: ConfigProvider) {
        reloadTimer.cancel()
    }
}
```

## Parsers
There are two steps in order to use a new parser (this is mostly used for parsing basic types and interfaces should be used instead of parsers!).

1. Create your class by implementing Parser
1. Register your parser in the `Parsers` class `addParser(), addClassedParser(), addParseredParser()`

```kotlin
data class Point(val x: Int, val y: Int)

object PointParser: Parser<Point> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = Point(value.split(',')[0], value.split(',')[1])
}

Parsers.addParser(Point::class.java, PointParser())
```

Alternative:
```kotlin
interface Point {
    val x: Int
    val y: Int
}

//And use it! that's everything you need!
```

Full example inside the sample module: https://github.com/jdiazcano/cfg4k/tree/master/sample

\* Have in mind that not everything is supported in code coverage in Kotlin (inline extension functions) so the code coverage might appear worse than it really is!

# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
