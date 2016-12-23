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

import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class CachedConfigProviderTest: Spek({

    val loaders = listOf(
            PropertyConfigLoader(javaClass.classLoader.getResource("test.properties")),
            JsonConfigLoader(javaClass.classLoader.getResource("test.json"))
    )
    loaders.forEach { loader ->
        describe("a property config loader [${loader.javaClass.name}]") {
            val providers = listOf(
                    DefaultConfigProvider(loader),
                    CachedConfigProvider(DefaultConfigProvider(loader))
            )
            providers.forEach { provider ->
                it("primitive properties [${provider.javaClass.name}]") {
                    provider.getProperty("integerProperty", Int::class.java).should.be.equal(1)
                    provider.getProperty("integerProperty", Integer::class.java).should.be.equal(Integer(1))
                    provider.getProperty("longProperty", Long::class.java).should.be.equal(2)
                    provider.getProperty("shortProperty", Short::class.java).should.be.equal(1)
                    provider.getProperty("floatProperty", Float::class.java).should.be.equal(2.1F)
                    provider.getProperty("doubleProperty", Double::class.java).should.be.equal(1.1)
                    provider.getProperty("byteProperty", Byte::class.java).should.be.equal(2)
                    provider.getProperty("booleanProperty", Boolean::class.java).should.be.`true`
                }

                it("binding test [${provider.javaClass.name}]") {
                    val testBinder = provider.bind<TestBinder>("")
                    testBinder.booleanProperty().should.be.`true`
                    testBinder.integerProperty().should.be.equal(1)
                    testBinder.longProperty().should.be.equal(2)
                    testBinder.shortProperty().should.be.equal(1)
                    testBinder.floatProperty().should.be.equal(2.1F)
                    testBinder.doubleProperty().should.be.equal(1.1)
                    testBinder.byteProperty().should.be.equal(2)
                    testBinder.a().should.be.equal("b")
                    testBinder.c().should.be.equal("d")
                    testBinder.list().should.be.equal(listOf(1, 2, 3))
                    testBinder.floatList().should.be.equal(listOf(1.2F, 2.2F, 3.2F))

                    val secondTestBinder = provider.bind<TestBinder>("")
                    when (provider) {
                        // When we have a cached object it must be the same object, but when it's not cached values must
                        is CachedConfigProvider -> testBinder.should.be.equal(secondTestBinder)
                        is DefaultConfigProvider -> {
                            with (testBinder) {
                                booleanProperty().should.be.equal(secondTestBinder.booleanProperty())
                                integerProperty().should.be.equal(secondTestBinder.integerProperty())
                                longProperty().should.be.equal(secondTestBinder.longProperty())
                                shortProperty().should.be.equal(secondTestBinder.shortProperty())
                                floatProperty().should.be.equal(secondTestBinder.floatProperty())
                                doubleProperty().should.be.equal(secondTestBinder.doubleProperty())
                                byteProperty().should.be.equal(secondTestBinder.byteProperty())
                                a().should.be.equal(secondTestBinder.a())
                                c().should.be.equal(secondTestBinder.c())
                                list().should.be.equal(secondTestBinder.list())
                                floatList().should.be.equal(secondTestBinder.floatList())
                            }
                        }
                        else -> IllegalArgumentException("Unrecognised provider")
                    }
                }
            }
        }
    }
})
