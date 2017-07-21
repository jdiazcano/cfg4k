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

package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.parsers.Parser
import com.jdiazcano.cfg4k.parsers.Parsers
import com.jdiazcano.cfg4k.providers.CachedConfigProvider
import com.jdiazcano.cfg4k.providers.ProxyConfigProvider
import com.jdiazcano.cfg4k.utils.ParserClassNotFound
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
                    ProxyConfigProvider(loader),
                    CachedConfigProvider(ProxyConfigProvider(loader))
            )
            providers.forEach { provider ->
                it("a class is not registered") {
                    assertFailsWith<ParserClassNotFound> {
                        provider.get("harrypotter", Book::class.java)
                    }
                }

                it("class has been registered and now everything is cool") {
                    Parsers.addParser(Book::class.java, BookParser)
                    provider.get("harrypotter", Book::class.java).should.be.equal(Book(1, "Prisoner of azkaban"))
                }
            }
        }
    }
})

data class Book(val id: Long, val title: String)
object BookParser : Parser<Book> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): Book {
        val obj = value.asObject()
        return Book(obj["id"]!!.asString().toLong(), obj["title"]!!.asString())
    }

}