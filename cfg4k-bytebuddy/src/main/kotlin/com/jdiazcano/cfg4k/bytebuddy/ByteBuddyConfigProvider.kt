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
import com.jdiazcano.cfg4k.binders.createCollection
import com.jdiazcano.cfg4k.binders.getDefaultMethod
import com.jdiazcano.cfg4k.binders.getPropertyName
import com.jdiazcano.cfg4k.binders.isMethodNullable
import com.jdiazcano.cfg4k.binders.toMutableCollection
import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.parsers.Parsers.findParser
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.reloadstrategies.ReloadStrategy
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.jdiazcano.cfg4k.utils.TargetType
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.matcher.ElementMatchers.isDeclaredBy
import net.bytebuddy.matcher.ElementMatchers.not
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass

open class ByteBuddyConfigProvider(
        configLoader: ConfigLoader,
        reloadStrategy: ReloadStrategy? = null
) : DefaultConfigProvider(configLoader, reloadStrategy, ByteBuddyBinder())

@Suppress("UNCHECKED_CAST")
class ByteBuddyBinder : Binder {

    val cache = WeakHashMap<Class<*>, Class<*>>()

    override fun <T : Any> bind(configProvider: ConfigProvider, prefix: String, type: Class<T>): T {
        if (type in cache) {
            return cache[type]!!.getDeclaredConstructor(ConfigProvider::class.java).newInstance(configProvider) as T
        }

        var subclass = ByteBuddy().subclass(type, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                .defineField("provider", ConfigProvider::class.java, Visibility.PRIVATE)
                .defineConstructor(Visibility.PUBLIC).withParameters(ConfigProvider::class.java)
                .intercept(MethodCall.invoke(Any::class.java.getConstructor()).andThen(FieldAccessor.ofField("provider").setsArgumentAt(0)))
        var instance: Any? = null
        type.methods.forEach { method ->

            subclass = subclass
                    .defineMethod(method.name, method.returnType, Modifier.PUBLIC)
                    .intercept(MethodDelegation
                            .withEmptyConfiguration()
                            .filter(not(isDeclaredBy(Any::class.java)))
                            .to(object : Any() {
                                @RuntimeType fun delegate() = Handler<T>(configProvider, method, prefix, instance).delegate()
                            }))
        }
        val generatedClass = subclass.make()
                .load(javaClass.classLoader)
                .loaded
        cache[type] = generatedClass
        instance = generatedClass
                .getDeclaredConstructor(ConfigProvider::class.java)
                .newInstance(configProvider)
        return instance
    }
}

class Handler<T>(
        val configProvider: ConfigProvider,
        val method: Method,
        val prefix: String,
        val instance: Any?,
        val kotlinClass: KClass<*> = method.declaringClass.kotlin,
        val name: String = getPropertyName(method.name),
        val returnType: Type = method.genericReturnType) {
    @RuntimeType
    fun delegate(): T? {
        val isNullable = kotlinClass.isMethodNullable(method, name)
        var returning: Any?
        val configObject = configProvider.load(concatPrefix(prefix, name))
        if (configObject == null) {
            try {
                returning = kotlinClass.getDefaultMethod(method.name)?.invoke(instance, instance)
            } catch (e: Exception) { // There's no default
                if (isNullable) {
                    returning = null
                } else {
                    throw SettingNotFound("Setting $name not found")
                }
            }
        } else {
            if (configObject.isArray()) {
                val targetType = TargetType(returnType)
                val rawType = targetType.rawTargetType()
                val collection = createCollection(rawType)
                toMutableCollection(configObject, returnType, collection, name, configProvider, prefix)
                returning = collection
            } else if (configObject.isPrimitive()) {
                val targetType = TargetType(returnType)
                val rawType = targetType.rawTargetType()
                val superType = targetType.getParameterizedClassArguments().firstOrNull()
                val classType = superType ?: rawType
                returning = classType.findParser().parse(configObject, classType, superType?.findParser())
            } else { // it is an object
                returning = configProvider.bind(concatPrefix(prefix, name), method.returnType)
            }
        }
        return returning as T?
    }
}

fun Providers.bytebuddy(loader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ByteBuddyConfigProvider(loader, reloadStrategy)