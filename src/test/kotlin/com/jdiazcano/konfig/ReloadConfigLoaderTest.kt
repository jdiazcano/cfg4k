package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.JsonConfigLoader
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


})