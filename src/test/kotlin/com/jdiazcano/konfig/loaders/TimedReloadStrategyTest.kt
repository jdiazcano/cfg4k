package com.jdiazcano.konfig.loaders

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
            var lastReload = 1
            val lastIteration = 7
            for (i in 1..10) {
                if (i > lastIteration) {
                    provider.cancelReload()
                    lastReload = lastIteration // This is the last reload iteration (8-1)
                }
                file.writeText(text.replace("%reload1", "b$i").replace("%reload2", "d$lastReload"))
                Thread.sleep(1000)
                provider.getProperty("a", String::class.java).should.be.equal("b$lastReload")
                provider.getProperty("c", String::class.java).should.be.equal("d$lastReload")
                lastReload++
            }
            file.delete()
        }
    }

})
