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
package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.EnvironmentConfigLoader
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.SystemPropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.OverrideConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigLoaderTest : Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("test.properties")))
        it("a value should be b") {
            loader.get("a").should.be.equal("b".toConfig())
        }
    }

    describe("a environment config loader") {
        beforeEachTest {
            // The environment variables will be set in travis
            if (System.getenv()["CI_NAME"]?:"" != "travis-ci") {
                setEnv("JAVAHOME", "myjavahome")
                setEnv("FOO_BAR", "bar")
                setEnv("FOO_BAR_MORE", "morebar")
                setEnv("NESTED_FOO_BAR", "nestedbar")
            }
        }

        it("variables should have the values of the javadoc comment") {
            val loader = EnvironmentConfigLoader()
            loader.get("foo.bar").should.be.equal("bar".toConfig())
            loader.get("javahome").should.be.equal("myjavahome".toConfig())
            loader.get("foo.bar.more").should.be.equal("morebar".toConfig())
            loader.get("nested.foo.bar").should.be.equal("nestedbar".toConfig())
            loader.get("nested.foo.bar").should.be.equal("nestedbar".toConfig()) // This should be a cached property!

            loader.get("this.will.be.not.found").should.be.`null`

            loader.reload() // This is mostly for coverage, I haven't seen that environment can be reloaded
        }
    }

    describe("a system property config loader") {
        beforeEachTest {
            System.setProperty("this.is.a.test", "testvalue")
            System.setProperty("another.test", "bestest")
        }

        it("settings should have the above values") {
            val loader = SystemPropertyConfigLoader()
            loader.get("this.is.a.test").should.be.equal("testvalue".toConfig())
            loader.get("another.test").should.be.equal("bestest".toConfig())
        }
    }

    describe("an override with system and environment") {
        beforeEachTest {
            System.setProperty("this.is.a.test", "testvalue")
            System.setProperty("another.test", "bestest")

            // The environment variables will be set in travis
            if (System.getenv()["CI_NAME"]?:"" != "travis-ci") {
                setEnv("SERVER_HOST", "localhost")
                setEnv("SERVER_ENVIRONMENT", "development")
            }
        }

        it("should be ok") {
            val sysLoader = SystemPropertyConfigLoader()
            val envLoader = EnvironmentConfigLoader()

            val baseProvider = OverrideConfigProvider(
                    DefaultConfigProvider(sysLoader),
                    DefaultConfigProvider(envLoader)
            )

            baseProvider.get<String>("server.host").should.be.equal("localhost")
            baseProvider.get<String>("server.environment").should.be.equal("development")
        }
    }
})

private interface Config {
    val database: DatabaseConfig
    val server: ServerConfig
}

private interface DatabaseConfig {
    val url: String
    val username: String
    val password: String
}

private interface ServerConfig {
    val port: Int
    val host: String
    val environment: String
}

internal fun setEnv(key: String, value: String) {
    try {
        val env = System.getenv()
        val cl = env.javaClass
        val field = cl.getDeclaredField("m")
        field.isAccessible = true
        val writableEnv = field.get(env) as MutableMap<String, String>
        writableEnv.put(key, value)
    } catch (e: Exception) {
        throw IllegalStateException("Failed to set environment variable", e)
    }

}