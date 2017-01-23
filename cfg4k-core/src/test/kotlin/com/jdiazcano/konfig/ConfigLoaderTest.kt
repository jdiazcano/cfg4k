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

package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.EnvironmentConfigLoader
import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.loaders.SystemPropertyConfigLoader
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigLoaderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("test.properties"))
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
        }
    }

    describe("a json config loader") {
        val loader = JsonConfigLoader(javaClass.classLoader.getResource("test.json"))
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
            loader.get("nested.a").should.be.equal("nestedb")
        }
    }

    /**
     * If this test fails you need to add the next environment variables to your build/system/whatever
     *
     * JAVAHOME => myjavahome
     * FOO_BAR => bar
     * FOO_BAR_MORE => morebar
     * NESTED_FOO_BAR => nestedbar
     */
    describe("a environment config loader") {
        val loader = EnvironmentConfigLoader()
        it("variables should have the values of the javadoc comment") {
            loader.get("foo.bar").should.be.equal("bar")
            loader.get("javahome").should.be.equal("myjavahome")
            loader.get("foo.bar.more").should.be.equal("morebar")
            loader.get("nested.foo.bar").should.be.equal("nestedbar")
            loader.get("nested.foo.bar").should.be.equal("nestedbar") // This should be a cached property!

            loader.get("this.will.be.not.found").should.be.equal("")

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
            loader.get("this.is.a.test").should.be.equal("testvalue")
            loader.get("another.test").should.be.equal("bestest")
        }
    }
})