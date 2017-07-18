package com.jdiazcano.hocon

import com.jdiazcano.cfg4k.hocon.HoconConfigLoader
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.providers.bind
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class ProviderWithHocon: Spek({
    describe("a hocon loader and provider test") {
        val loader = HoconConfigLoader("hocon.conf")
        val provider = Providers.proxy(loader)
        val bind = provider.bind<KtorDeploymentConfig>("ktor.deployment")
        bind.port.should.be.equal(8080)
        bind.host.should.be.`null`
        bind.ssl.port.should.be.`null`
        bind.ssl.keyStore.should.be.equal("goodKeyStore")
        bind.watch.should.be.equal(listOf("hello", "bye"))
    }
})

interface KtorDeploymentConfig {
    val host: String?
    val port: Int get() = 80
    val watch: List<String>
    val ssl: KtorDeploymentSslConfig
}

interface KtorDeploymentSslConfig {
    val port: Int?
    val keyStore: String
}