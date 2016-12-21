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
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class EnumBindingTest: Spek({

    describe("a property config loader") {
        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("enumtest.properties"))
        val provider = DefaultConfigProvider(loader)

        it("binding test") {
            val testBinder = provider.bind("", BindedEnum::class.java)
            testBinder.thisWillBeEnum().should.be.equal(TestEnum.TEST)
        }

        it("prefixed binding test") {
            val testBinder = provider.bind("prefixed", PrefixedBindedEnum::class.java)
            testBinder.enumtest().should.be.equal(TestEnum.TEST2)
        }

        it("multiple prefix enum binding") {
            val testBinder = provider.bind("another.prefixed.double", PrefixedBindedEnum::class.java)
            testBinder.enumtest().should.be.equal(TestEnum.TEST1)
        }
    }
})

