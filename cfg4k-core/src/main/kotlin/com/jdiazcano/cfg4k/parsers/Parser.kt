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

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.utils.TypeStructure

/**
 * Base Parser interface, not all the implementations will use all the parameters but they will be there in case they
 * are needed.
 */
interface Parser<out T> {

    /**
     * Parses a string into a Type
     *
     * @param context The configuration context with the current provider
     * @param value The string that comes from the source
     * @param typeStructure The structure of the types and generic types
     */
    fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure = TypeStructure(Any::class.java)): T
}