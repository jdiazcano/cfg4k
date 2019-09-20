package com.jdiazcano.cfg4k.reloadstrategies

import com.jdiazcano.cfg4k.binders.ProxyBinder
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.FileConfigSource
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import java.nio.file.Files

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

    feature("a provider with a filechange reloader (with a symlink chain)") {
        // simulate a Kubernetes ConfigMap setup
        val tempDir = Files.createTempDirectory("cfg4k")
        tempDir.toFile().let {
            it.mkdirs()
            it.deleteOnExit()
        }

        val numbered1 = Files.createDirectory(tempDir.resolve("..10001"))
        val numbered2 = Files.createDirectory(tempDir.resolve("..10002"))
        val dataLn = Files.createSymbolicLink(tempDir.resolve("..data"), tempDir.relativize(numbered1))

        val file1 = numbered1.resolve("reloadedfile.properties").toFile()
        file1.writeText("foo=bar")

        val file2 = numbered2.resolve("reloadedfile.properties").toFile()
        file2.writeText("foo=bar2")

        val configFileLn = Files.createSymbolicLink(tempDir.resolve("reloadedfile.properties"),
                tempDir.relativize(dataLn).resolve("reloadedfile.properties"))

        val provider = DefaultConfigProvider(
                PropertyConfigLoader(FileConfigSource(configFileLn)),
                FileChangeReloadStrategy(configFileLn)
        )

        scenario("should have this config loaded") {
            provider.get<String>("foo").shouldBe("bar")
        }

        scenario("should update the configuration when a soft link is updated") {
            // change the ..data link like Kubernetes does
            Files.delete(dataLn)
            Files.createSymbolicLink(tempDir.resolve("..data"), tempDir.relativize(numbered2))

            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered

            provider.get<String>("foo").shouldBe("bar2")

            provider.cancelReload()

            // change the ..data link like Kubernetes does
            Files.delete(dataLn)
            Files.createSymbolicLink(tempDir.resolve("..data"), tempDir.relativize(numbered1))

            Thread.sleep(5000) // We need to wait a little bit for the event watcher to be triggered

            provider.get<String>("foo").shouldBe("bar2") // We have canceled the reload so no more reloads
        }
    }
})
