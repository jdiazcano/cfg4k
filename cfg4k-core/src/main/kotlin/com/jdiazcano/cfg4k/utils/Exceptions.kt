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

package com.jdiazcano.cfg4k.utils

/**
 * This exception will be thrown when a parser is not defined for a specific class. If you see this error you need to
 * call the methods "addParser" "addClassedParser" of the Provider
 */
class SettingNotFound(message: String) : Exception(message)

/**
 * Thrown when the settings have not been initialised/loaded into memory and you try to access them.
 */
class SettingsNotInitialisedException(message: String): Exception(message)

/**
 * This exception will be thrown when a parser is not defined for a specific class. If you see this error you need to
 * call the methods "addParser" "addClassedParser" of the Provider
 */
class ParserClassNotFound(message: String) : Exception(message)