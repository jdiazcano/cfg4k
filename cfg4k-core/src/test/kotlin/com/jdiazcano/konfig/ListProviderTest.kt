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
import com.jdiazcano.konfig.providers.ProxyConfigProvider
import com.jdiazcano.konfig.providers.bind
import com.jdiazcano.konfig.providers.getProperty
import com.jdiazcano.konfig.utils.typeOf
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ListProviderTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("listtest.properties"))
        val jsonLoader = JsonConfigLoader(javaClass.classLoader.getResource("test.json"))
        val provider = ProxyConfigProvider(loader)
        val jsonProvider = ProxyConfigProvider(jsonLoader)

        it("Simple property test") {
            val testBinder: List<Int> = provider.getProperty("list", typeOf<List<Int>>())
            testBinder.should.be.equal(listOf(1, 2, 3, 4, 5, 6, 7))
            val betterIntList: List<Int> = jsonProvider.getProperty("betterIntList", typeOf<List<Int>>())
            betterIntList.should.be.equal(listOf(1, 2, 100))
            val betterStringList: List<String> = jsonProvider.getProperty("betterStringList", typeOf<List<String>>())
            betterStringList.should.be.equal(listOf("a", "b", "c"))
            val betterEnumList = jsonProvider.getProperty<List<Enumerito>>("betterEnumList")
            betterEnumList.should.be.equal(listOf(Enumerito.A, Enumerito.B))
        }

        it("prefixed binding test") {
            val testBinder = provider.bind<Binded>("prefixed")
            testBinder.list().should.be.equal(listOf(1, 2, 3, 4, 5, 6, 7))
            testBinder.set().should.be.equal(setOf(1, 2, 3, 4, 5, 6, 7))
            testBinder.enumerito().should.be.equal(listOf(Enumerito.A, Enumerito.B))
        }

    }
})

