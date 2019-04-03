package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.providers.*
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class EnvironmentConfigLoaderTest : Spek({

    describe("a property config loader") {
        mockkStatic(System::class)
        every { System.getenv() } returns mapOf(
                "PROPERTIES_GROUPONE_KEYONE" to "1",
                "PROPERTIES_GROUPONE_KEYTWO" to "2"
        )

        val loader = EnvironmentConfigLoader()
        val provider = ProxyConfigProvider(loader)

        it("it should be good in the loader") {
            loader.get("properties.groupone.keyone").should.be.equal("1".toConfig())
        }

        it ("should be also good with the provider") {
            provider.get<String>("properties.groupone.keyone").should.be.equal("1")
        }

        it("works with binding") {
            val properties = provider.bind<Props>("properties")
            properties.groupone.keyone.should.be.equal("1")
            properties.groupone.keytwo.should.be.equal("2")
        }

        unmockkAll()
    }

})

interface Props {
    val groupone: Groupone
}

interface Groupone {
    val keyone: String
    val keytwo: String
}