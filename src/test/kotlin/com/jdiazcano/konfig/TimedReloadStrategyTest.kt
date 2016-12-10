package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.TimedReloadStrategy
import com.jdiazcano.konfig.providers.DefaultConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.concurrent.TimeUnit

class TimedReloadStrategyTest : Spek({
    val text = """
{
  "a": "%reload1",
  "c": "%reload2",
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
            """
    describe("a timed reloadable json config loader") {
        val file = File("timedreloadedfile.json")
        file.createNewFile()
        file.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
        val provider = DefaultConfigProvider(JsonConfigLoader(file.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS))
        it("properties should be equals") {
            provider.getProperty("a", String::class.java).should.be.equal("b")
            provider.getProperty("c", String::class.java).should.be.equal("d")
            for (i in 1..10) {
                file.writeText(text.replace("%reload1", "b$i").replace("%reload2", "d$i"))
                Thread.sleep(1000)
                provider.getProperty("a", String::class.java).should.be.equal("b$i")
                provider.getProperty("c", String::class.java).should.be.equal("d$i")
            }
        }
    }

})
