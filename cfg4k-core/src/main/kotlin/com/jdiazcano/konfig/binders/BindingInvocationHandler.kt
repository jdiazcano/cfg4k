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

package com.jdiazcano.konfig.binders

import com.jdiazcano.konfig.parsers.Parsers.canParse
import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.providers.bind
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.reflect.functions

/**
 * InvocationHandler that handles the proxying between the interface and the call. This class is used in the
 * ProxyConfigProvider.
 */
class BindingInvocationHandler(
        private val provider: ConfigProvider,
        private val prefix: String
): InvocationHandler {

    private val objectMethods: List<String> = Object::class.functions.map { it.name }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        if (objectMethods.contains(method.name)) {
            return method.invoke(this, *(args?: arrayOf()))
        }

        val type = method.genericReturnType
        if (canParse(method.returnType.kotlin)) {
            return provider.getProperty(prefix(prefix, method.name), type)
        } else {
            return provider.bind(prefix(prefix, method.name), method.returnType.kotlin)
        }

    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other?.hashCode()
    }

}

fun prefix(before: String, after: String): String {
    return buildString {
        append(before)
        if (before.isNotEmpty()) {
            append('.')
        }
        append(after)
    }
}