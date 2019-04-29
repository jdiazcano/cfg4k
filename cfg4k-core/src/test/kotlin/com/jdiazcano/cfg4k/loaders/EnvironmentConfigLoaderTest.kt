package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.toConfig
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll

class EnvironmentConfigLoaderTest: StringSpec() {

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        mockkStatic(System::class)
        every { System.getenv() } returns mapOf(
                "PROPERTIES_GROUPONE_KEYONE" to "1",
                "PROPERTIES_GROUPONE_KEYTWO" to "2"
        )
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        unmockkAll()
    }

    init {
        val loader by lazy { EnvironmentConfigLoader() }

        "it should be good in the loader" {
            loader.get("properties.groupone.keyone") shouldBe "1".toConfig()
        }

        "null if not found" {
            loader.get("this.is.not.found").shouldBeNull()
        }

        "updated when reloading" {
            loader.get("properties.groupone.keyone") shouldBe "1".toConfig()
            every { System.getenv() } returns mapOf(
                    "PROPERTIES_GROUPONE_KEYONE" to "11",
                    "PROPERTIES_GROUPONE_KEYTWO" to "22"
            )
            loader.reload()
            loader.get("properties.groupone.keyone") shouldBe "11".toConfig()
        }
    }
}