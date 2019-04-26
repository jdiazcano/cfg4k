package com.jdiazcano.cfg4k.core

import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.io.InvalidObjectException

class ConfigObjectTest : StringSpec({

    "string config object merge" {
        val first = "a".toConfig()
        val second = "a".toConfig()

        first.isString() shouldBe true
        shouldThrow<InvalidObjectException> {
            first.merge(second)
        }
    }

    "map config object merge" {
        val first = mapOf("a" to "b").toConfig()
        val second = mapOf("b" to "c").toConfig()

        first.isObject() shouldBe true
        first.merge(second) shouldBe mapOf("a" to "b", "b" to "c").toConfig()
    }

    "map config object merge throws if two string values" {
        val first = mapOf("a" to "b").toConfig()
        val second = mapOf("a" to "c").toConfig()

        shouldThrow<InvalidObjectException> {
            first.merge(second) shouldBe mapOf("a" to "b", "a" to "c").toConfig()
        }
    }

    "map config object merge with lists" {
        val first = mapOf("a" to listOf("b", "c")).toConfig()
        val second = mapOf("a" to listOf("c", "d")).toConfig()

        first.merge(second) shouldBe mapOf("a" to listOf("b", "c", "d")).toConfig()
    }

    "map config object merge with lists and first objects aren't modified" {
        val first = mapOf("a" to listOf("b", "c")).toConfig()
        val second = mapOf("a" to listOf("c", "d")).toConfig()
        val merge = first.merge(second)

        first shouldBe mapOf("a" to listOf("b", "c")).toConfig()
        second shouldBe mapOf("a" to listOf("c", "d")).toConfig()
    }

    "list merge" {
        val first = listOf("a").toConfig()
        val second = listOf("b").toConfig()

        first.merge(second) shouldBe listOf("a", "b").toConfig()
    }

    "nested merge" {
        val list1 = MapConfigObject(mapOf(
                "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig())),
                "b" to MapConfigObject(mapOf(
                        "aa" to 11.toConfig(),
                        "ab" to 22.toConfig(),
                        "ac" to 33.toConfig()
                ))
        ))

        val list2 = MapConfigObject(mapOf(
                "a" to ListConfigObject(listOf(3.toConfig())),
                "c" to 3.toConfig(),
                "d" to 4.toConfig()
        ))

        val mergedList = list1.merge(list2)
        mergedList shouldBe MapConfigObject(mapOf(
                        "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig(), 3.toConfig())),
                        "b" to MapConfigObject(mapOf(
                                "aa" to 11.toConfig(),
                                "ab" to 22.toConfig(),
                                "ac" to 33.toConfig()
                        )),
                        "c" to 3.toConfig(),
                        "d" to 4.toConfig()
                ))
    }

    "config to string" {
        1.toConfig().toString() shouldBe "ConfigObject(value=1)"
        "a".toConfig().toString() shouldBe "ConfigObject(value=a)"
        true.toConfig().toString() shouldBe "ConfigObject(value=true)"
        1L.toConfig().toString() shouldBe "ConfigObject(value=1)"
        1.1.toConfig().toString() shouldBe "ConfigObject(value=1.1)"
        1.1F.toConfig().toString() shouldBe "ConfigObject(value=1.1)"
    }

    "extension converters" {
        1.toConfig() shouldBe StringConfigObject("1")
        "a".toConfig() shouldBe StringConfigObject("a")
        true.toConfig() shouldBe StringConfigObject("true")
        1L.toConfig() shouldBe StringConfigObject("1")
        1.1.toConfig() shouldBe StringConfigObject("1.1")
        1.1F.toConfig() shouldBe StringConfigObject("1.1")
    }

})