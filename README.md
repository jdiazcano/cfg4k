[![Build Status](https://travis-ci.org/jdiazcano/cfg4k.svg?branch=master)](https://travis-ci.org/jdiazcano/cfg4k) [![Coverage Status](https://coveralls.io/repos/github/jdiazcano/cfg4k/badge.svg?branch=master)](https://coveralls.io/github/jdiazcano/cfg4k?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Release](https://jitpack.io/v/jdiazcano/cfg4k.svg)](https://jitpack.io/#jdiazcano/cfg4k)

#Overview 
Cfg4k is a configuration library made for Kotlin in Kotlin!

Features
* Automatic reload
* Interface binding
* Huge flexibility, custom sources
* Easy to use
* Performance matters, the new Bytebuddy provider will be able to compile your bindings at runtime

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

# Detailed features

## Providers
1. DefaultConfigProvider: Base class for all the other providers. It has the basic functionality for a Provider. (You can obviously ignore it if implementing a new provider)
4. CachedConfigProvider: This provider will cache the calls into a map and use the cached one once the same call is done again. When the method `reload` is called this cache will be cleared.
5. OverrideConfigProvider: With this provider you can input a list of Loaders in order of precedence and it will return the first one that is not null/empty. So you can override properties for example: `EnvironmentConfigLoader -> JsonConfigLoader` it would pick first from Environment and then from Json.

## Binders
1. ProxyBinder: Uses Java `InvocationHandler` in order to implement the interface and intercept the calls to return the correct value.
2. ByteBuddyBinder: Uses `ByteBuddy` to create a class that implements the interface and returns the value.

## Loaders
1. JsonConfigLoader: Load the properties from a Json file, an URL must be provided.
2. PropertyConfigLoader: Load the properties from a Java properties file.
3. EnvironmentConfigLoader: Load the properties from the Environment Variables. This will transform the environment variables so they can be used inside the project and stay consistent with the rest of the properties. For example having the variable `GLOBAL_URL` will result in the property `global.url`
    1. `_` will become `.`
    2. `-` will become `.`
    3. The property name will become lowercase
4. HoconConfigLoader: Load the properties from a Hocon file. This has many advantages over Json or Property (it is a superset of both) so I recommend this loader but not before taking a look at the project: https://github.com/typesafehub/config#using-hocon-the-json-superset
5. YamlConfigLoader: Load the properties from a Yaml file.
6. GitConfigLoader: Load the properties from a Git repository, possible giving the branch name or folder to denote different environments.

## Reload strategies
1. TimedReloadStrategy: This will reload the properties on a time basis.

## Parsers
These parsers are supported out of the box

1. Int
2. Long
3. Double
4. Short
5. Float
6. Double
7. Byte
8. String
9. Boolean
10. List<Any>
11. Set<Any>
12. Enum
13. BigInteger
14. BigDecimal
15. LocalDateTime
16. LocalDate
17. ZonedDateTime
18. OffsetDateTime
19. OffsetTime
20. Calendar
21. Date

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

    override fun <T : Any> getProperty(name: String, type: Class<T>, default: T?): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property = configProvider.getProperty(name, type, default)
            cache[name] = property
            return property
        }
    }

    override fun <T: Any> getProperty(name: String, type: Typable, default: T?): T {
        if (cache.containsKey(name)) {
            return cache[name] as T
        } else {
            val property: T = configProvider.getProperty(name, type, default)
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
You can create your own config loader by implementing the `ConfigLoader` interface
```kotlin
open class SystemPropertyConfigLoader : ConfigLoader {
    override fun reload() {
        // Nothing to do, the System.properties do the reload for us!
    }

    override fun get(key: String): String {
        return System.getProperty(key, "")
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
There are two steps in order to use a new parser.

1. Create your class by implementing Parser
2. Register your parser in the `Parsers` class `addParser(), addClassedParser(), addParseredParser()`

```kotlin
data class Point(val x: Int, val y: Int)

object PointParser: Parser<Point> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>) = Point(value.split(',')[0], value.split(',')[1])
}

Parsers.addParser(Point::class.java, PointParser())
```

A more complicated example

```kotlin
class PrinterClassedParser : Parser<Printer> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): Printer {
        val split = value.split('-')
        return Printer(split[0], split[1].toInt())
    }
}

class Printer(name: String, age: Int) : Person(name, age) {
    fun print() = super.toString()
}

open class Person(val name: String, val age: Int) {
    override fun toString() = "$name, $age"

    // Override and equals needed for comparing in tests
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Person

        if (name != other.name) return false
        if (age != other.age) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age
        return result
    }

}

Parsers.addClassedParser(Person::class.java, PrinterClassedParser())
```

Full example inside the sample module: https://github.com/jdiazcano/cfg4k/tree/master/sample

\* Have in mind that not everything is supported in code coverage in Kotlin (inline extension functions) so the code coverage might appear worse than it really is!

# License
Licensed under the Apache License, Version 2.0. See LICENSE file.
