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

import java.math.BigDecimal
import java.math.BigInteger

interface TestBinder {
    fun integerProperty(): Int
    fun a(): String
    fun c(): String
    fun booleanProperty(): Boolean
    fun longProperty(): Long
    fun shortProperty(): Short
    fun doubleProperty(): Double
    fun floatProperty(): Float
    fun byteProperty(): Byte
    fun list(): List<Int>
    fun floatList(): List<Float>
    fun bigIntegerProperty(): BigInteger
    fun bigDecimalProperty(): BigDecimal
}

enum class TestEnum {
    TEST, TEST1, TEST2
}

interface BindedEnum {
    fun thisWillBeEnum(): TestEnum
}

interface PrefixedBindedEnum {
    fun enumtest(): TestEnum
}

interface Binded {
    fun list(): List<Int>
    fun set(): Set<Int>
    fun enumerito(): List<Enumerito>
}

enum class Enumerito {
    A, B, C
}

interface NestedBinder {
    fun nested(): TestBinder
    fun normal(): Int
}

interface SuperNested {
    fun supernested(): NestedBinder
    fun normal(): Int
}