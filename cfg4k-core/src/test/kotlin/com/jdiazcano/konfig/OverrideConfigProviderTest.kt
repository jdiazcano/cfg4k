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

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.providers.Providers.overriden
import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.providers.getProperty
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class OverrideConfigProviderTest : Spek({
    val provider = overriden(
            DefaultConfigProvider(
                    PropertyConfigLoader(javaClass.classLoader.getResource("overridetest.properties"))
            ),
            DefaultConfigProvider(
                    PropertyConfigLoader(javaClass.classLoader.getResource("test.properties"))
            )
    )
    describe("An overriding provider") {
        it("if the property exist in the first, should not go to the second loader") {
            provider.getProperty("a", String::class).should.be.equal("overrideb")
            provider.getProperty("a", String::class).should.be.equal("overrideb") // cached property!
            provider.getProperty("c", String::class).should.be.equal("overrided")
        }

        it("if the property does not exist, then the second one should be tested") {
            provider.getProperty<Int>("integerProperty").should.be.equal(1)
            provider.getProperty<List<Enumerito>>("enumList").should.be.equal(listOf(Enumerito.A, Enumerito.B))
            provider.getProperty<List<Enumerito>>("enumList").should.be.equal(listOf(Enumerito.A, Enumerito.B)) // cache
            provider.getProperty<List<Int>>("list").should.be.equal(listOf(1, 2, 3))
            provider.bind<TestBinder>("").floatList().should.be.equal(listOf(1.2F, 2.2F, 3.2F))
        }
    }
})