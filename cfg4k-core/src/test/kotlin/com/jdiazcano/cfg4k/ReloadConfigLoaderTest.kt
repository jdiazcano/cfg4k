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

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.SystemPropertyConfigLoader
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class ReloadConfigLoaderTest : Spek({
    describe("a reloadable properties config loader") {
        val file = File("reloadedfile.properties")
        file.createNewFile()
        file.deleteOnExit()
        file.writeText("""
a=b
c=d
integerProperty=1
longProperty=2
shortProperty=1
doubleProperty=1.1
floatProperty=2.1
byteProperty=2
list=1,2,3
booleanProperty=true
nested.a=nestedb""")

        val loader = PropertyConfigLoader(FileConfigSource(file))
        it("a value should be b") {
            loader.get("a").should.be.equal("b".toConfig())
            loader.get("nested.a").should.be.equal("nestedb".toConfig())
        }

        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
a=reloadedb
c=reloadedd
integerProperty=1
longProperty=2
shortProperty=1
doubleProperty=1.1
floatProperty=2.1
byteProperty=2
list=1,2,3
booleanProperty=true
nested.a=reloaded nestedb""")

            loader.reload()
            loader.get("a").should.be.equal("reloadedb".toConfig())
            loader.get("nested.a").should.be.equal("reloaded nestedb".toConfig())
        }
    }

    describe("a reloadable properties config loader") {
        val file = File("reloadedfile.properties")
        file.createNewFile()
        file.writeText("""
{
a=b
c=d
integerProperty=1
longProperty=2
shortProperty=1
doubleProperty=1.1
floatProperty=2.1
byteProperty=2
list=1,2,3
booleanProperty=true
nested.a=nestedb
}
        """)

        val loader = PropertyConfigLoader(FileConfigSource(file))
        it("a value should be b") {
            loader.get("a").should.be.equal("b".toConfig())
            loader.get("nested.a").should.be.equal("nestedb".toConfig())
        }

        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
a=reloadedb
c=reloadedd
integerProperty=1
longProperty=2
shortProperty=1
doubleProperty=1.1
floatProperty=2.1
byteProperty=2
list=1,2,3
booleanProperty=true
nested.a=reloaded nestedb
            """)

            loader.reload()
            loader.get("a").should.be.equal("reloadedb".toConfig())
            loader.get("nested.a").should.be.equal("reloaded nestedb".toConfig())

            file.delete()
        }
    }

    describe("a reloadable properties config loader") {
        val file = File("reloadedfile.properties")
        file.createNewFile()
        file.writeText("""
a=b
c=d""".trim())

        val loader = PropertyConfigLoader(FileConfigSource(file))
        it("values should be equals like in the string") {
            loader.get("a").should.be.equal("b".toConfig())
            loader.get("c").should.be.equal("d".toConfig())
        }

        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
a=reloadedb
c=reloadedd
            """.trim())

            loader.reload()
            loader.get("a").should.be.equal("reloadedb".toConfig())
            loader.get("c").should.be.equal("reloadedd".toConfig())

            file.delete()
        }
    }

    describe("a reloadable system properties config loader") {
        val loader = SystemPropertyConfigLoader()
        var inc = 1
        beforeEachTest {
            System.setProperty("first", "$inc")
            System.setProperty("second", "$inc")
            inc++
            loader.reload()
        }

        it("the first time should be 1 and 1") {
            loader.let {
                it.get("first").should.be.equal("1".toConfig())
                it.get("second").should.be.equal("1".toConfig())
            }
        }

        it("the first time should be 2 and 2") {
            loader.let {
                it.get("first").should.be.equal("2".toConfig())
                it.get("second").should.be.equal("2".toConfig())
            }

        }
    }

})