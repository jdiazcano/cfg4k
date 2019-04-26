package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.toConfig
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll

class SystemPropertyConfigLoaderTest : StringSpec() {

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        mockkStatic(System::class)
        every { System.getProperties() } returns mapOf(
                "properties.groupone.keyone" to "1",
                "properties.groupone.keytwo" to "2"
        ).toProperties()
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        unmockkAll()
    }

    init {
        val loader by lazy { SystemPropertyConfigLoader() }

        "it should be good in the loader" {
            loader.get("properties.groupone.keyone").shouldBe("1".toConfig())
        }

        "null if not found" {
            loader.get("this.is.not.found").shouldBeNull()
        }

        "updated when reloading" {
            loader.get("properties.groupone.keyone") shouldBe "1".toConfig()
            every { System.getProperties() } returns mapOf(
                    "properties.groupone.keyone" to "11",
                    "properties.groupone.keytwo" to "22"
            ).toProperties()
            loader.reload()
            loader.get("properties.groupone.keyone") shouldBe "11".toConfig()
        }
    }
}