package com.jdiazcano.hocon

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.hocon.toConfig
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class HoconConfigMapperTest : Spek({
    describe("a hocon loader that can map") {
        val conf = ConfigFactory.parseResourcesAnySyntax("hocon.conf", ConfigParseOptions.defaults())
        val configObject = conf.toConfig()

        configObject.isObject().should.be.`true`
        val ktor = configObject.asObject()["ktor"]!!
        val deployment = ktor.asObject()["deployment"]!!
        deployment.isObject().should.be.`true`
        deployment.asObject()["port"].should.be.equal(8080.toConfig())
        deployment.asObject()["watch"].should.be.equal(ConfigObject(listOf(
                "hello".toConfig(), "bye".toConfig()
        )))
        deployment.asObject()["ssl"]!!.asObject()["keyStore"].should.be.equal("goodKeyStore".toConfig())

        val app = ktor.asObject()["application"]!!
        app.isObject().should.be.`true`
        app.asObject()["modules"].should.be.equal(ConfigObject(listOf(
                "com.jdiazcano.hocon.KtorConfig".toConfig()
        )))
    }
})