package com.jdiazcano.hocon

import com.jdiazcano.cfg4k.hocon.HoconConfigLoader
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.providers.bind
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class ProviderWithMaps : Spek({
    describe("a hocon loader and provider test") {
        val loader = HoconConfigLoader("hocon.conf")
        val provider = Providers.proxy(loader)
        val bind = provider.bind<KtorConfig>("ktor")

        val commands = bind.commands

        commands["test"]!!.isgoodcommand.should.be.`true`
        commands["secondtest"]!!.isgoodcommand.should.be.`true`
        commands["thirdtest"]!!.isgoodcommand.should.be.`false`
    }
})