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

import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.loaders.JsonConfigLoader
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.SystemPropertyConfigLoader
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class ReloadConfigLoaderTest : Spek({
    describe("a reloadable json config loader") {
        val file = File("reloadedfile.json")
        file.createNewFile()
        file.writeText("""
{
  "a": "b",
  "c": "d",
  "integerProperty": 1,
  "longProperty": 2,
  "shortProperty": 1,
  "doubleProperty": 1.1,
  "floatProperty": 2.1,
  "byteProperty": 2,
  "list": "1,2,3",
  "booleanProperty": true,
  "nested": {
    "a": "nestedb"
  }
}
        """)

        val loader = JsonConfigLoader(file.toURI().toURL())
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
            loader.get("nested.a").should.be.equal("nestedb")
        }


        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
{
  "a": "reloadedb",
  "c": "reloadedd",
  "integerProperty": 1,
  "longProperty": 2,
  "shortProperty": 1,
  "doubleProperty": 1.1,
  "floatProperty": 2.1,
  "byteProperty": 2,
  "list": "1,2,3",
  "booleanProperty": true,
  "nested": {
    "a": "reloaded nestedb"
  }
}
            """)

            loader.reload()
            loader.get("a").should.be.equal("reloadedb")
            loader.get("nested.a").should.be.equal("reloaded nestedb")

            file.delete()
        }
    }

    describe("a reloadable json config loader") {
        val file = File("reloadedfile.json")
        file.createNewFile()
        file.writeText("""
{
  "a": "b",
  "c": "d",
  "integerProperty": 1,
  "longProperty": 2,
  "shortProperty": 1,
  "doubleProperty": 1.1,
  "floatProperty": 2.1,
  "byteProperty": 2,
  "list": "1,2,3",
  "booleanProperty": true,
  "nested": {
    "a": "nestedb"
  }
}
        """)

        val loader = JsonConfigLoader(file.toURI().toURL())
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
            loader.get("nested.a").should.be.equal("nestedb")
        }


        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
{
  "a": "reloadedb",
  "c": "reloadedd",
  "integerProperty": 1,
  "longProperty": 2,
  "shortProperty": 1,
  "doubleProperty": 1.1,
  "floatProperty": 2.1,
  "byteProperty": 2,
  "list": "1,2,3",
  "booleanProperty": true,
  "nested": {
    "a": "reloaded nestedb"
  }
}
            """)

            loader.reload()
            loader.get("a").should.be.equal("reloadedb")
            loader.get("nested.a").should.be.equal("reloaded nestedb")

            file.delete()
        }
    }

    describe("a reloadable properties config loader") {
        val file = File("reloadedfile.properties")
        file.createNewFile()
        file.writeText("""
a=b
c=d""".trim())

        val loader = PropertyConfigLoader(file.toURI().toURL())
        it("values should be equals like in the string") {
            loader.get("a").should.be.equal("b")
            loader.get("c").should.be.equal("d")
        }


        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
a=reloadedb
c=reloadedd
            """.trim())

            loader.reload()
            loader.get("a").should.be.equal("reloadedb")
            loader.get("c").should.be.equal("reloadedd")

            file.delete()
        }
    }

    describe("a reloadable system properties config loader") {
        var loader: ConfigLoader? = null
        var inc = 1
        beforeEachTest {
            System.setProperty("first", "$inc")
            System.setProperty("second", "$inc")
        }

        it("the first time should be 1 and 2") {
            loader = SystemPropertyConfigLoader()
            loader?.let {
                it.get("first").should.be.equal("1")
                it.get("second").should.be.equal("1")
            }
        }

        it("the first time should be 1 and 2") {
            loader?.let {
                it.reload()
                it.get("first").should.be.equal("2")
                it.get("second").should.be.equal("2")
            }

        }
    }


})