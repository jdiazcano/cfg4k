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

package com.jdiazcano.konfig.bytebuddy

import com.jdiazcano.konfig.binding.prefix
import com.jdiazcano.konfig.loaders.ConfigLoader
import com.jdiazcano.konfig.loaders.ReloadStrategy
import com.jdiazcano.konfig.parsers.Parsers
import com.jdiazcano.konfig.providers.AbstractConfigProvider
import com.jdiazcano.konfig.providers.Providers
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import java.lang.reflect.Modifier
import net.bytebuddy.implementation.bind.annotation.RuntimeType

import net.bytebuddy.implementation.MethodDelegation

@Suppress("UNCHECKED_CAST")
open class ByteBuddyConfigProvider(
        configLoader: ConfigLoader,
        reloadStrategy: ReloadStrategy? = null
): AbstractConfigProvider(configLoader, reloadStrategy) {
    val cache = mutableMapOf<String, Any>()

    override fun <T: Any> bind(prefix: String, type: Class<T>): T {
        if (cache.containsKey(prefix)) {
            return cache[prefix] as T
        }

        var subclass = ByteBuddy().subclass(type, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
        type.methods.forEach { method ->

            val returnType = method.genericReturnType

            val value: () -> T = {
                if (Parsers.canParse(method.returnType)) {
                    getProperty(prefix(prefix, method.name), returnType)
                } else {
                    bind(prefix(prefix, method.name), method.returnType) as T
                }
            }
            subclass = subclass

                    .defineMethod(method.name, method.returnType, Modifier.PUBLIC)
                    .intercept(MethodDelegation
                            .to(object : Any() { @RuntimeType fun delegate() = value.invoke() }))
        }

        val instance = subclass.make().load(javaClass.classLoader).loaded.newInstance()
        cache[prefix] = instance
        return instance
    }

}

fun Providers.Companion.bytebuddy(loader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ByteBuddyConfigProvider(loader, reloadStrategy)