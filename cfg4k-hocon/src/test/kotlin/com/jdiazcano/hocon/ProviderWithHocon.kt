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
        val bind = provider.bind<KtorConfig>("ktor")
        bind.deployment().port.should.be.equal(8080)
        bind.deployment().host.should.be.`null`
        bind.deployment().ssl.port.should.be.`null`
        bind.deployment().ssl.keyStore.should.be.equal("goodKeyStore")
        bind.deployment().watch.should.be.equal(listOf("hello", "bye"))
        bind.users.size.should.be.equal(2)
        bind.users[0].age().should.be.equal(1)
        bind.users[1].age().should.be.equal(100)
        bind.users[0].name.should.be.equal("pepe")
        bind.users[1].name.should.be.equal("thefrog")
        bind.application.modules.size.should.be.equal(1)
        bind.application.modules[0].should.be.equal(KtorConfig::class.java)
    }
})

interface KtorConfig {
    fun deployment(): KtorDeploymentConfig
    val users: List<User>
    val application: KtorModules
}

interface KtorModules {
    val modules: List<Class<*>>
}

interface KtorDeploymentConfig {
    val host: String?
    val port: Int get() = 80
    val watch: List<String>
    val ssl: KtorDeploymentSslConfig
}

interface User {
    val name: String
    fun age(): Int
}

interface KtorDeploymentSslConfig {
    val port: Int?
    val keyStore: String
}