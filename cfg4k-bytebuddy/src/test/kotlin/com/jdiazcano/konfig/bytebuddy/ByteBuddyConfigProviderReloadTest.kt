package com.jdiazcano.konfig.bytebuddy

import com.jdiazcano.konfig.ConfigProvider
import com.jdiazcano.konfig.bind
import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.TimedReloadStrategy
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.concurrent.TimeUnit

class ByteBuddyConfigProviderReloadTest: Spek({
    val text = """{
  "a": "%reload1",
  "c": "%reload2",
  "nested": {
    "a": "reloaded nestedb"
  }
}"""
    describe("a timed reloadable bytebuddy proxy with json config loader") {
        it("bytebuddy test") {
            val file = File("timedreloadedfile.json")
            file.createNewFile()
            file.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val provider = ByteBuddyConfigProvider(JsonConfigLoader(file.toURI().toURL()), TimedReloadStrategy(1, TimeUnit.SECONDS))
            checkProvider(file, provider, text)
        }

    }
})

private fun checkProvider(file: File, provider: ConfigProvider, text: String, overriden: Boolean = false) {
    val bindedProperty = provider.bind<Normal>("")
    bindedProperty.nested().a().should.be.equal("reloaded nestedb")
    if (overriden) {
        bindedProperty.a().should.be.equal("overrideb")
    } else {
        bindedProperty.a().should.be.equal("b")
    }
    bindedProperty.c().should.be.equal("d")
    var lastReload = 1
    val lastIteration = 3
    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration (8-1)
        }
        if (overriden) {
            file.writeText(text.replace("%reload1", "overrideb$lastReload").replace(",\n  \"c\": \"%reload2\"", ""))
        } else {
            file.writeText(text.replace("%reload1", "b$lastReload").replace("%reload2", "d$lastReload"))
        }
        Thread.sleep(1500)
        if (overriden) {
            bindedProperty.a().should.be.equal("overrideb$lastReload")
            bindedProperty.c().should.be.equal("d")
        } else {
            bindedProperty.a().should.be.equal("b$lastReload")
            bindedProperty.c().should.be.equal("d$lastReload")
        }
        lastReload++
    }
    file.delete()
}

interface Nested {
    fun a(): String
}

interface Normal {
    fun nested(): Nested
    fun a(): String
    fun c(): String
}