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

package com.jdiazcano.konfig.providers

import com.jdiazcano.konfig.ConfigLoader
import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.binding.BindingInvocationHandler
import com.jdiazcano.konfig.loaders.ReloadStrategy
import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.parsers.Parsers.findParser
import com.jdiazcano.konfig.parsers.Parsers.getClassedParser
import com.jdiazcano.konfig.parsers.Parsers.getParser
import com.jdiazcano.konfig.parsers.Parsers.getParseredParser
import com.jdiazcano.konfig.parsers.Parsers.isClassedParser
import com.jdiazcano.konfig.parsers.Parsers.isParser
import com.jdiazcano.konfig.parsers.Parsers.isParseredParser
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.jdiazcano.konfig.utils.TargetType
import com.jdiazcano.konfig.utils.Typable
import java.lang.reflect.Proxy

@Suppress("UNCHECKED_CAST")
open class ProxyConfigProvider(
        configLoader: ConfigLoader,
        reloadStrategy: ReloadStrategy? = null
): AbstractConfigProvider(configLoader, reloadStrategy) {

    override fun <T: Any> bind(prefix: String, type: Class<T>): T {
        val handler = BindingInvocationHandler(this, prefix)
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }

}
