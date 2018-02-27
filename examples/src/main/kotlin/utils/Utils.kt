/*
 *   * Copyright 2016-2018 Javier Díaz-Cano Martín-Albo (javierdiazcanom@gmail.com)  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.
 */

package utils

import com.jdiazcano.cfg4k.binders.Binder
import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.sources.StringConfigSource
import schoolJson

fun createSchoolProvider(binder: Binder = ProxyBinder()): DefaultConfigProvider {
    val source = StringConfigSource(schoolJson)           // 1- Define the source
    val loader = JsonConfigLoader(source)                 // 2- Define HOW you want to load it (as Json in this case)
    return DefaultConfigProvider(loader, binder = binder) // 3- Create a provider that will let you get/bind
}

fun createProvider(string: String): DefaultConfigProvider {
    val source = StringConfigSource(string)
    val loader = JsonConfigLoader(source)
    return DefaultConfigProvider(loader)
}