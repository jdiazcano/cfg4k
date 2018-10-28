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

package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.binders.Binder
import com.jdiazcano.cfg4k.binders.concatPrefix
import com.jdiazcano.cfg4k.binders.convert
import com.jdiazcano.cfg4k.binders.getDefaultMethod
import com.jdiazcano.cfg4k.binders.getPropertyName
import com.jdiazcano.cfg4k.binders.isMethodNullable
import com.jdiazcano.cfg4k.binders.objectMethods
import com.jdiazcano.cfg4k.binders.overridableAnyMethods
import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.reloadstrategies.ReloadStrategy
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.jdiazcano.cfg4k.utils.convert
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.FieldValue
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.WeakHashMap

open class ByteBuddyConfigProvider(
        configLoader: ConfigLoader,
        reloadStrategy: ReloadStrategy? = null
) : DefaultConfigProvider(configLoader, reloadStrategy, ByteBuddyBinder())

@Suppress("UNCHECKED_CAST")
class ByteBuddyBinder : Binder {

    private val cache = WeakHashMap<Class<*>, Class<*>>()

    override fun <T : Any> bind(configProvider: ConfigProvider, prefix: String, type: Class<T>): T {
        if (type in cache) {
            return cache[type]!!.getDeclaredConstructor(
                    ConfigProvider::class.java,
                    String::class.java,
                    Class::class.java).newInstance(configProvider, prefix, type) as T
        }

        var subclass = ByteBuddy().subclass(type, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineField("provider", ConfigProvider::class.java, Visibility.PRIVATE)
                .defineField("prefix", String::class.java, Visibility.PRIVATE)
                .defineField("type", Class::class.java, Visibility.PRIVATE)
                .defineConstructor(Visibility.PUBLIC).withParameters(ConfigProvider::class.java, String::class.java, Class::class.java)
                .intercept(
                        MethodCall.invoke(Any::class.java.getConstructor())
                                .andThen(FieldAccessor.ofField("provider").setsArgumentAt(0)
                                .andThen(FieldAccessor.ofField("prefix").setsArgumentAt(1)
                                .andThen(FieldAccessor.ofField("type").setsArgumentAt(2)))
                        )
                )

        // Interfaces only return the decalred methods and classes return everything. If we add the methods no matter what
        // then we end up duplicating some methods
        val methods = type.methods + (if (type.isInterface) overridableAnyMethods else arrayOf<Method>())
        methods.forEach { method ->
            subclass = subclass
                    .defineMethod(method.name, method.returnType, Modifier.PUBLIC)
                    .withParameters(*method.parameterTypes)
                    .intercept(MethodDelegation
                            .withDefaultConfiguration()
                            .to(ConfigurationHandler::class.java))
        }

        val generatedClass = subclass.make()
                .load(javaClass.classLoader)
                .loaded
        cache[type] = generatedClass
        return generatedClass
                .getDeclaredConstructor(ConfigProvider::class.java, String::class.java, Class::class.java)
                .newInstance(configProvider, prefix, type)
    }
}

class ConfigurationHandler {

    companion object {
        @JvmStatic
        @RuntimeType
        fun <T> intercept(
                @This that: Any,
                @AllArguments args: Array<Any>?,
                @Origin classMethod: Method,
                @FieldValue("provider") provider: ConfigProvider,
                @FieldValue("type") interfaze: Class<T>,
                @FieldValue("prefix") prefix: String): Any? {

            when (classMethod.name) {
                "toString" -> return provider.load(prefix).toString()
                in objectMethods -> return classMethod.invoke(this, *(args ?: arrayOf()))
            }

            val method = interfaze.getMethod(classMethod.name)
            val kotlinClass = method.declaringClass.kotlin
            val propertyName = getPropertyName(method.name)
            val type = method.genericReturnType
            val isNullable = kotlinClass.isMethodNullable(method, propertyName)
            var returning: Any?

            val qualifiedPropertyName = concatPrefix(prefix, propertyName)
            val configObject = provider.load(qualifiedPropertyName)
            if (configObject == null) {
                try {
                    returning = kotlinClass.getDefaultMethod(method.name)?.invoke(that, that)
                } catch (e: Exception) { // There's no default
                    if (isNullable) {
                        returning = null
                    } else {
                        throw SettingNotFound(qualifiedPropertyName)
                    }
                }
            } else {
                val structure = type.convert()
                val context = ConfigContext(provider, qualifiedPropertyName)
                returning = convert(context, configObject, structure)
            }
            return returning as T?
        }
    }
}

fun Providers.bytebuddy(loader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ByteBuddyConfigProvider(loader, reloadStrategy)
