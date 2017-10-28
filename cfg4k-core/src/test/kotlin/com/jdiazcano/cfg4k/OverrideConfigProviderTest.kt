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

import com.jdiazcano.cfg4k.loaders.EnvironmentConfigLoader
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.SystemPropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.Providers.overriden
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class OverrideConfigProviderTest : Spek({
    System.setProperty("integerProperty", "1")
    // The environment variables will be set in travis
    if (System.getenv()["CI_NAME"]?:"" != "travis-ci") {
        setEnv("INTEGERPROPERTY", "1")
    }

    val provider = overriden(
            DefaultConfigProvider(
                    PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("overridetest.properties")))
            ),
            DefaultConfigProvider(
                    PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("test.properties")))
            )
    )

    val environmentOverride = overriden(
            DefaultConfigProvider(
                    EnvironmentConfigLoader()
            ),
            DefaultConfigProvider(
                    PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("overridetest.properties")))
            )
    )

    val systemOverride = overriden(
            DefaultConfigProvider(
                    SystemPropertyConfigLoader()
            ),
            DefaultConfigProvider(
                    PropertyConfigLoader(URLConfigSource(javaClass.classLoader.getResource("overridetest.properties")))
            )

    )

    describe("An overriding provider") {
        it("if the property exist in the first, should not go to the second loader") {
            provider.get("a", String::class.java).should.be.equal("overrideb")
            provider.get("a", String::class.java).should.be.equal("overrideb") // cached property!
            provider.get("c", String::class.java).should.be.equal("overrided")
            environmentOverride.get("a", String::class.java).should.be.equal("overrideb")
            environmentOverride.get("a", String::class.java).should.be.equal("overrideb") // cached property!
            environmentOverride.get("c", String::class.java).should.be.equal("overrided")
            systemOverride.get("a", String::class.java).should.be.equal("overrideb")
            systemOverride.get("a", String::class.java).should.be.equal("overrideb") // cached property!
            systemOverride.get("c", String::class.java).should.be.equal("overrided")

            systemOverride.reload()
            systemOverride.get("a", String::class.java).should.be.equal("overrideb")
            systemOverride.get("a", String::class.java).should.be.equal("overrideb") // cached property!
            systemOverride.get("c", String::class.java).should.be.equal("overrided")
        }

        it("if the property does not exist, then the second one should be tested") {
            provider.get<Int>("integerProperty").should.be.equal(1)
            environmentOverride.get<Int>("integerProperty").should.be.equal(1)
            systemOverride.get<Int>("integerProperty").should.be.equal(1)
        }
    }
})