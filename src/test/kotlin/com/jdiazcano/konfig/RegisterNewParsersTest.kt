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

package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.providers.CachedConfigProvider
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.jdiazcano.konfig.utils.ParserClassNotFound
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

class RegisterNewParsersTest: Spek({

    describe("a property config loader") {
        val loaders = listOf(
                PropertyConfigLoader(javaClass.classLoader.getResource("book.properties"))
        )
        loaders.forEach { loader ->
            val providers = listOf(
                    DefaultConfigProvider(loader),
                    CachedConfigProvider(DefaultConfigProvider(loader))
            )
            providers.forEach { provider ->
                it("a class is not registered") {
                    assertFailsWith<ParserClassNotFound> {
                        provider.getProperty("harrypotter", Book::class.java)
                    }
                }

                it("class has been registered and now everything is cool") {
                    provider.addParser(Book::class.java, BookParser)
                    provider.getProperty("harrypotter", Book::class.java).should.be.equal(Book(1, "Prisoner of azkaban"))
                }
            }
        }
    }
})

data class Book(val id: Long, val title: String)
object BookParser : Parser<Book> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): Book {
        val (id, title) = value.split(", ")
        return Book(id.toLong(), title)
    }

}