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

package com.jdiazcano.cfg4k.utils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TypableTests : StringSpec({
    "Testing the toString of Typable" {
        typeOf<List<String>>().toString().shouldBe("java.util.List<? extends java.lang.String>")
        typeOf<List<Int>>().toString().shouldBe("java.util.List<? extends java.lang.Integer>")
        typeOf<Int>().toString().shouldBe("class java.lang.Integer")
        typeOf<Map<String, List<Int>>>().toString().shouldBe("java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.Integer>>")
    }
})