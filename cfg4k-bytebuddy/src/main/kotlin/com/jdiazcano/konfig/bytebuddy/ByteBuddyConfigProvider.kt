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

import com.jdiazcano.konfig.binders.Binder
import com.jdiazcano.konfig.binders.prefix
import com.jdiazcano.konfig.loaders.ConfigLoader
import com.jdiazcano.konfig.providers.ConfigProvider
import com.jdiazcano.konfig.reloadstrategies.ReloadStrategy
import com.jdiazcano.konfig.parsers.Parsers
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.providers.Providers
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.implementation.*
import java.lang.reflect.Modifier
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.matcher.ElementMatchers.isDeclaredBy
import net.bytebuddy.matcher.ElementMatchers.not


@Suppress("UNCHECKED_CAST")
open class ByteBuddyConfigProvider(
        configLoader: ConfigLoader,
        reloadStrategy: ReloadStrategy? = null
): DefaultConfigProvider(configLoader, reloadStrategy, ByteBuddyBinder())

@Suppress("UNCHECKED_CAST")
class ByteBuddyBinder : Binder {
    override fun <T : Any> bind(provider: ConfigProvider, prefix: String, type: Class<T>): T {
        var subclass = ByteBuddy().subclass(type, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
        type.methods.forEach { method ->

            val returnType = method.genericReturnType

            val value: () -> T = {
                if (Parsers.canParse(method.returnType)) {
                    provider.getProperty(prefix(prefix, method.name), returnType)
                } else {
                    provider.bind(prefix(prefix, method.name), method.returnType) as T
                }
            }
            subclass = subclass

                    .defineMethod(method.name, method.returnType, Modifier.PUBLIC)
                    .intercept(MethodDelegation
                            .withEmptyConfiguration()
                            .filter(not(isDeclaredBy(Any::class.java)))
                            .to(object : Any() { @RuntimeType fun delegate() = value() }))
        }
        val instance = subclass.make().load(javaClass.classLoader).loaded.newInstance()
        return instance
    }
}

fun Providers.Companion.bytebuddy(loader: ConfigLoader, reloadStrategy: ReloadStrategy? = null) = ByteBuddyConfigProvider(loader, reloadStrategy)