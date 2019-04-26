package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.EnvironmentConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.net.InetAddress
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.util.*

class CommonParsersTest: StringSpec({
    val emptyContext = ConfigContext(DefaultConfigProvider(EnvironmentConfigLoader()), "")

    "pattern parser" {
        PatternParser.parse(emptyContext, "a.*".toConfig()).pattern().shouldBe("a.*")
    }

    "RegexParser" {
        RegexParser.parse(emptyContext, "a.*".toConfig()).pattern.shouldBe("a.*")
    }

    "UUIDParser" {
        UUIDParser.parse(emptyContext,
                "c796118e-8a67-4eb7-adcf-62132a2f095d".toConfig())
                .shouldBe(UUID.fromString("c796118e-8a67-4eb7-adcf-62132a2f095d"))
    }

    "SQLDriverParser" {
        mockkStatic(DriverManager::class)
        val driver = mockk<Driver>()
        every { DriverManager.getDriver(any()) } returns driver

        SQLDriverParser.parse(emptyContext, "random".toConfig()).shouldBe(driver)

        unmockkStatic(DriverManager::class)
    }

    "SQLConnectionParser" {
        mockkStatic(DriverManager::class)
        val driver = mockk<Connection>()
        every { DriverManager.getConnection(any()) } returns driver

        SQLConnectionParser.parse(emptyContext, "random".toConfig()).shouldBe(driver)

        unmockkStatic(DriverManager::class)
    }

    "InetAddressParser" {
        InetAddressParser.parse(emptyContext, "127.0.0.1".toConfig()).shouldBe(InetAddress.getByName("127.0.0.1"))
    }
})