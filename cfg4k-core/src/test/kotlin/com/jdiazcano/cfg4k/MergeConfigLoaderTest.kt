package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.ClasspathConfigSource
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class MergeConfigLoaderTest : Spek({

    describe("a config loader that can be merged") {
        val firstConfigSource = ClasspathConfigSource("/first.properties")
        val secondConfigSource = ClasspathConfigSource("/second.properties")
        val configLoaderOne = PropertyConfigLoader(firstConfigSource)
        val configLoaderTwo = PropertyConfigLoader(secondConfigSource)
        val provider = DefaultConfigProvider(configLoaderOne.merge(configLoaderTwo))

        provider.get<Int>("int").should.be.equal(1)
        provider.get<Int>("paco").should.be.equal(2)
        provider.get<Int>("pepe").should.be.equal(2)
        provider.get<String>("pico").should.be.equal("Test")
    }
})