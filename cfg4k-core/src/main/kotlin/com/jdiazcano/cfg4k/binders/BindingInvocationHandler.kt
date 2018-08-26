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

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.jdiazcano.cfg4k.utils.convert
import java.io.InvalidObjectException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.LinkedList
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

val objectMethods: List<String> = Object::class.java.declaredMethods.map { it.name }
val overridableAnyMethods = Any::class.java.methods.filter { it.name == "toString" }.toTypedArray()

/**
 * InvocationHandler that handles the proxying between the interface and the call. This class is used in the
 * ProxyConfigProvider.
 */
class BindingInvocationHandler(
        private val provider: ConfigProvider,
        private val prefix: String
) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {

        when (method.name) {
            "toString" -> return provider.load(prefix).toString()
            in objectMethods -> return method.invoke(this, *(args ?: arrayOf()))
        }

        val propertyName = getPropertyName(method.name)
        val kotlinClass = method.declaringClass.kotlin
        val isNullable = kotlinClass.isMethodNullable(method, propertyName)

        val type = method.genericReturnType
        val configObject = provider.load(concatPrefix(prefix, propertyName))
        return if (configObject == null) {
            try {
                kotlinClass.getDefaultMethod(method.name)?.invoke(this, proxy)
            } catch (e: Exception) { // There's no default
                if (isNullable) {
                    null
                } else {
                    throw SettingNotFound(propertyName)
                }
            }
        } else {
            val structure = type.convert()
            val context = ConfigContext(provider, concatPrefix(prefix, propertyName))
            convert(context, configObject, structure)
        }

    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other?.hashCode()
    }

}

fun createCollection(rawType: Class<*>): MutableCollection<Any?> {
    return when {
        ArrayList::class.java.isAssignableFrom(rawType) -> arrayListOf()
        LinkedList::class.java.isAssignableFrom(rawType) -> mutableListOf()
        LinkedHashSet::class.java.isAssignableFrom(rawType) -> mutableSetOf()
        HashSet::class.java.isAssignableFrom(rawType) -> hashSetOf()
        List::class.java.isAssignableFrom(rawType) -> arrayListOf()
        Set::class.java.isAssignableFrom(rawType) -> mutableSetOf()
        else -> throw InvalidObjectException("Invalid class to create a collection from.")
    }
}

fun KClass<*>.getDefaultMethod(methodName: String): Method? {
    return Class.forName("$jvmName\$DefaultImpls").methods.firstOrNull { it.name == methodName }
}

fun KClass<*>.isMethodNullable(method: Method, propertyName: String = ""): Boolean {
    val properties = memberProperties.filter {
        it.name == propertyName || it.name == method.name
    }
    return properties.firstOrNull()?.returnType?.isMarkedNullable
            ?: method.kotlinFunction?.returnType?.isMarkedNullable
            ?: false
}

private val METHOD_NAME_REGEX = "^(get|is|has)?(.*)".toRegex()

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

fun concatPrefix(before: String, after: String): String {
    return buildString {
        append(before)
        if (before.isNotEmpty()) {
            append('.')
        }
        append(after)
    }
}