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

package com.jdiazcano.cfg4k.binders

import com.jdiazcano.cfg4k.parsers.Parsers.isParseable
import com.jdiazcano.cfg4k.providers.ConfigProvider
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.test.assertTrue

/**
 * InvocationHandler that handles the proxying between the interface and the call. This class is used in the
 * ProxyConfigProvider.
 */
class BindingInvocationHandler(
        private val provider: ConfigProvider,
        private val prefix: String
): InvocationHandler {

    private val objectMethods: List<String> = Object::class.java.declaredMethods.map { it.name }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val name = getPropertyName(method.name)
        if (objectMethods.contains(method.name)) {
            return method.invoke(this, *(args?: arrayOf()))
        }

        val type = method.genericReturnType
        if (method.returnType.isParseable()) {
            return provider.getProperty(prefix(prefix, name), type)
        } else {
            return provider.bind(prefix(prefix, name), method.returnType)
        }

    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other?.hashCode()
    }

}

private val METHOD_NAME_REGEX = "^(get|is|has)?(.*)".toRegex()

fun main(args: Array<String>) {
    assertTrue { getPropertyName("getTest") == "test" }
    assertTrue { getPropertyName("isomorphic") == "isomorphic" }
    assertTrue { getPropertyName("isOmorphic") == "omorphic" }
    assertTrue { getPropertyName("hash") == "hash" }
    assertTrue { getPropertyName("hasTests") == "tests" }
    assertTrue { getPropertyName("getИмя") == "имя" }
    assertTrue { getPropertyName("getимя") == "getимя" }
}

fun getPropertyName(methodName: String): String {
    return METHOD_NAME_REGEX.replace(methodName) { matchResult ->
        val group = matchResult.groups[2]!!.value
        if (Character.isUpperCase(group[0])) {
            group.decapitalize()
        } else {
            methodName
        }
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