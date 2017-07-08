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

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.nio.file.Path

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
    fun uri(): URI
    fun url(): URL
    fun file(): File
    fun path(): Path
}

interface PropertyTestBinder {
    val integerProperty: Int
    val a: String
    val c: String
    val booleanProperty: Boolean
    val longProperty: Long
    val shortProperty: Short
    val doubleProperty: Double
    val floatProperty: Float
    val byteProperty: Byte
    val list: List<Int>
    val floatList: List<Float>
    val bigIntegerProperty: BigInteger
    val bigDecimalProperty: BigDecimal
    val uri: URI
    val url: URL
    val file: File
    val path: Path
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