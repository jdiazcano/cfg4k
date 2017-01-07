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
import com.jdiazcano.konfig.providers.ProxyConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class NestedBindingTest: Spek({
    describe("nested properties") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("nestedtest.properties"))
        val provider = ProxyConfigProvider(loader)
        it("this is testing a normal nested property, when calling nested then it shall return a binding for it") {
            val bind = provider.bind<NestedBinder>("")
            bind.nested().a().should.be.equal("b")
            bind.normal().should.be.equal(2)
        }
    }

    describe("super nested properties") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("supernestedtest.properties"))
        val provider = ProxyConfigProvider(loader)
        it("deeply nested property") {
            val bind = provider.bind<SuperNested>("")
            bind.supernested().nested().a().should.be.equal("b")
            bind.normal().should.be.equal(3)
        }
    }
})
