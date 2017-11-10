package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.reloadstrategies.FileChangeReloadStrategy
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths

class FileChangeReloadStrategyTest : Spek({
    describe("a provider with a filechange reloader") {
        val file = Paths.get("test.properties")
        file.toFile().deleteOnExit()
        file.toFile().writeText("foo=bar")
        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(file)),
                FileChangeReloadStrategy(file),
                ProxyBinder()
        )

        it("should have this config loaded") {
            provider.get<String>("foo").should.be.equal("bar")
        }

        it("should update the configuration") {
            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered
            file.toFile().writeText("foo=bar2")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2")
            provider.cancelReload()
            file.toFile().writeText("foo=bar3")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2") // We have canceled the reload so no more reloads
        }
    }

    describe("a provider with a filechange reloader (with subdirectory)") {
        val file = Paths.get("subdir", "reloadedfile.properties")

        file.parent.toFile().mkdir()
        file.parent.toFile().deleteOnExit()
        file.toFile().deleteOnExit()
        file.toFile().writeText("foo=bar")

        val unrelatedFile = Paths.get("subdir/subdir", "reloadedfile.properties")
        unrelatedFile.parent.toFile().mkdir()
        unrelatedFile.parent.toFile().deleteOnExit()
        unrelatedFile.toFile().deleteOnExit()
        unrelatedFile.toFile().writeText("foo=bar")
        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(file)),
                FileChangeReloadStrategy(file),
                ProxyBinder()
        )

        it("should have this config loaded") {
            provider.get<String>("foo").should.be.equal("bar")
        }

        it("should update the configuration") {
            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered
            file.toFile().writeText("foo=bar2")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2")
            provider.cancelReload()
            file.toFile().writeText("foo=bar3")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").should.be.equal("bar2") // We have canceled the reload so no more reloads
        }
    }
})