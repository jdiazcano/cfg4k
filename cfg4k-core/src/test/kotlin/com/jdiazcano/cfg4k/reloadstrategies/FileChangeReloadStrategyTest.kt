package com.jdiazcano.cfg4k.reloadstrategies

import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.FileConfigSource
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileChangeReloadStrategyTest : FeatureSpec({
    feature("a provider with a filechange reloader") {
        val file = Files.createTempFile("cfg4k", "test.properties")
        file.toFile().deleteOnExit()
        file.toFile().writeText("foo=bar")
        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(file)),
                FileChangeReloadStrategy(file),
                ProxyBinder()
        )

        scenario("should have this config loaded") {
            provider.get<String>("foo").shouldBe("bar")
        }

        scenario("should update the configuration") {
            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered
            file.toFile().writeText("foo=bar2")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").shouldBe("bar2")
            provider.cancelReload()
            file.toFile().writeText("foo=bar3")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").shouldBe("bar2") // We have canceled the reload so no more reloads
        }
    }

    feature("a provider with a filechange reloader (with subdirectory)") {
        val folder = Files.createTempDirectory("cfg4k").toFile()
        val file = folder.resolve("reloadedfile.properties")

        folder.mkdirs()
        folder.deleteOnExit()
        file.writeText("foo=bar")

        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(file)),
                FileChangeReloadStrategy(file)
        )

        scenario("should have this config loaded") {
            provider.get<String>("foo").shouldBe("bar")
        }

        scenario("should update the configuration") {
            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered
            file.writeText("foo=bar2")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").shouldBe("bar2")
            provider.cancelReload()
            file.writeText("foo=bar3")
            Thread.sleep(5000) // Another wait for that
            provider.get<String>("foo").shouldBe("bar2") // We have canceled the reload so no more reloads
        }
    }
})