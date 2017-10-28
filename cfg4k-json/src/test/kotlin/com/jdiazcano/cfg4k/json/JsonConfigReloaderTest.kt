package com.jdiazcano.cfg4k.json

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class JsonConfigReloaderTest: Spek({
    describe("a reloadable properties config loader") {
        val file = File("reloadedfile.properties")
        file.createNewFile()
        file.writeText("""
{
  "a": "b",
  "c": "d"
}
""".trim())

        val loader = JsonConfigLoader(FileConfigSource(file))
        it("values should be equals like in the string") {
            loader.get("a").should.be.equal("b".toConfig())
            loader.get("c").should.be.equal("d".toConfig())
        }

        it("now we should have a reloaded values") {
            file.delete()
            file.createNewFile()
            file.writeText("""
{
  "a": "reloadedb",
  "c": "reloadedd"
}
            """.trim())

            loader.reload()
            loader.get("a").should.be.equal("reloadedb".toConfig())
            loader.get("c").should.be.equal("reloadedd".toConfig())

            file.delete()
        }
    }
})