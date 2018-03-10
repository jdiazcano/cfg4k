package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.reloadstrategies.FileChangeReloadStrategy
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class FileChangeReloadStrategyTest : Spek({
    val folder = createTempDir("reloadsubdir")

    afterGroup {
        folder.deleteRecursively()
    }

    describe("a provider with a filechange reloader") {
        val file = folder.resolve("test.properties")
        file.writeText("foo=bar")
        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(file)),
                FileChangeReloadStrategy(file)
        )

        it("should have this config loaded") {
            provider.get<String>("foo").should.be.equal("bar")
        }

        it("should update the configuration") {
            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered
            file.writeText("foo=bar2")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2")
            provider.cancelReload()
            file.writeText("foo=bar3")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2") // We have canceled the reload so no more reloads
        }
    }
})