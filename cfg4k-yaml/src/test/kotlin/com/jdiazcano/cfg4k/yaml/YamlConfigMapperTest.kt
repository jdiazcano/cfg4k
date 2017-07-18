package com.jdiazcano.cfg4k.yaml

import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.yaml.snakeyaml.Yaml

class YamlConfigMapperTest: Spek({
    describe("should work too") {
        it("should work") {
            val config = javaClass.classLoader.getResource("test.yml").openStream().use {
                Yaml().load(it) as Map<String, Any>
            }.toConfig()

            config.asObject()["integerProperty"]!!.should.be.equal(1.toConfig())
            config.asObject()["fruits"]!!.should.be.equal(listOf("apple", "orange").toConfig())
            config.asObject()["nested"]!!.should.be.equal(mapOf("nesteda" to "nestedb", "nestedc" to "nestedd").toConfig())
        }
    }
})