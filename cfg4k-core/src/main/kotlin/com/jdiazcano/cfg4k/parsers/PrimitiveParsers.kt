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

package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigObject

object IntParser: Parser<Int> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toInt()
}

object LongParser: Parser<Long> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toLong()
}

object ShortParser: Parser<Short> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toShort()
}

object BooleanParser: Parser<Boolean> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toBoolean()
}

object FloatParser: Parser<Float> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toFloat()
}

object DoubleParser: Parser<Double> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toDouble()
}

object ByteParser: Parser<Byte> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString().toByte()
}

object StringParser: Parser<String> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = value.asString()
}