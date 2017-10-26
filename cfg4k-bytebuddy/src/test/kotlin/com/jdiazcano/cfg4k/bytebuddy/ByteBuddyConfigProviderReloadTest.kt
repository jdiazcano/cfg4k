package com.jdiazcano.cfg4k.bytebuddy

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.concurrent.TimeUnit

class ByteBuddyConfigProviderReloadTest : Spek({
    val text = """a=%reload1
c=%reload2
nested.a=reloaded nestedb
"""
    describe("a timed reloadable bytebuddy proxy with json config loader") {
        it("bytebuddy test") {
            val file = File("timedreloadedfile.properties")
            file.createNewFile()
            file.writeText(text.replace("%reload1", "b").replace("%reload2", "d"))
            val provider = ByteBuddyConfigProvider(PropertyConfigLoader(FileConfigSource(file)), TimedReloadStrategy(1, TimeUnit.SECONDS))
            checkProvider(file, provider, text)
        }

    }
})

private fun checkProvider(file: File, provider: ConfigProvider, text: String, overriden: Boolean = false) {
    val bindedNormal = provider.bind<Normal>("")
    bindedNormal.nested().a().should.be.equal("reloaded nestedb")

    val bindedProperty = provider.bind<Properties>("")
    bindedProperty.nested.a().should.be.equal("reloaded nestedb")

    if (overriden) {
        bindedNormal.a().should.be.equal("overrideb")
        bindedProperty.a.should.be.equal("overrideb")
    } else {
        bindedNormal.a().should.be.equal("b")
        bindedProperty.a.should.be.equal("b")
    }
    bindedNormal.c().should.be.equal("d")
    bindedProperty.c.should.be.equal("d")

    var lastReload = 1
    val lastIteration = 3
    for (i in 1..5) {
        if (i > lastIteration) {
            provider.cancelReload()
            lastReload = lastIteration // This is the last reload iteration (8-1)
        }
        if (overriden) {
            file.writeText(text.replace("%reload1", "overrideb$lastReload").replace("c=%reload2\n", ""))
        } else {
            file.writeText(text.replace("%reload1", "b$lastReload").replace("%reload2", "d$lastReload"))
        }
        Thread.sleep(1500)
        if (overriden) {
            bindedNormal.a().should.be.equal("overrideb$lastReload")
            bindedNormal.c().should.be.equal("d")
        } else {
            bindedNormal.a().should.be.equal("b$lastReload")
            bindedNormal.c().should.be.equal("d$lastReload")
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

interface Properties {
    val nested: Nested
    val a: String
    val c: String
}