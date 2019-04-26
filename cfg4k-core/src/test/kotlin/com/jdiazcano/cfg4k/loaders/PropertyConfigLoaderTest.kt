package com.jdiazcano.cfg4k.loaders

import com.jdiazcano.cfg4k.core.ListConfigObject
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.sources.StringRotationConfigSource
import io.kotlintest.*
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.specs.StringSpec

class PropertyConfigLoaderTest : StringSpec({

    val source = StringRotationConfigSource(listOf(
            "a=1",
            "a=2",
            "a\\u1aa",
            "a=1\na.b=2"
    ))
    val loader by lazy { PropertyConfigLoader(source) }

    "it should be good in the loader" {
        loader.get("a").shouldBe("1".toConfig())
    }

    "null if not found" {
        loader.get("this.is.not.found").shouldBeNull()
    }

    "updated when reloading" {
        loader.get("a") shouldBe "1".toConfig()
        loader.reload()
        loader.get("a") shouldBe "2".toConfig()
    }

    "error when reloading a bad file" {
        loader.get("a") shouldBe "2".toConfig()
        shouldThrow<IllegalArgumentException> {
            loader.reload()
        }
    }

    "simple map is converted to properties and config" {
        val config = mapOf(
                "a" to "1",
                "b" to "2"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "a" to 1.toConfig(),
                "b" to 2.toConfig()
        ))
    }

    "lists are parsed properly in properties" {
        val config = mapOf(
                "a[0]" to "1",
                "a[1]" to "2"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf("a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig()))))
    }

    "lists are parsed properly in properties with properties in the middle" {
        val config = mapOf(
                "a[0]" to "1",
                "randomProperty" to "random",
                "a[1]" to "2"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig())),
                "randomProperty" to "random".toConfig()
        ))
    }

    // There's still no ability of setting the index when defining a property
    "!lists are parsed properly in properties with properties in the middle reverse order declaration" {
        val config = mapOf(
                "a[1]" to "1",
                "randomProperty" to "random",
                "a[0]" to "2"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig())),
                "randomProperty" to "random".toConfig()
        ))
    }

    "lists inside objects are parsed properly in properties" {
        val config = mapOf(
                "me.friends[0]" to "A",
                "me.friends[1]" to "B"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "me" to MapConfigObject(mapOf(
                        "friends" to ListConfigObject(listOf("A".toConfig(), "B".toConfig()))
                ))
        ))
    }

    "lists inside objects with properties are parsed properly in properties" {
        val config = mapOf(
                "me.friends[0].name" to "A",
                "me.friends[1].name" to "B"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "me" to MapConfigObject(mapOf(
                        "friends" to ListConfigObject(listOf(
                                MapConfigObject(mapOf("name" to "A".toConfig())),
                                MapConfigObject(mapOf("name" to "B".toConfig()))
                        ))
                ))
        ))
    }

    "map is converted to properties and config" {
        val config = mapOf(
                "properties.groupone" to "1",
                "properties.groupone.keyone" to "1",
                "properties.groupone.keytwo" to "2"
        ).toProperties().toConfig()

        config shouldBe MapConfigObject(mapOf(
                "properties" to MapConfigObject(mapOf(
                        "groupone" to MapConfigObject(mapOf(
                                "keyone" to 1.toConfig(),
                                "keytwo" to 2.toConfig()
                        ))
                ))
        ))
    }
})