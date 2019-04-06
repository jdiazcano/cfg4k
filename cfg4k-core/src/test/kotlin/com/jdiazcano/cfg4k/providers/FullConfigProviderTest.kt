package com.jdiazcano.cfg4k.providers

import com.jdiazcano.cfg4k.InterfaceMethodWithAllTheThings
import com.jdiazcano.cfg4k.InterfacePropertyWithAllTheThings
import com.jdiazcano.cfg4k.ObjectWithAllTheThings
import com.jdiazcano.cfg4k.Potato
import com.jdiazcano.cfg4k.bytebuddy.ByteBuddyConfigProvider
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.hocon.HoconConfigLoader
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.sources.StringConfigSource
import com.jdiazcano.cfg4k.utils.SettingNotFound
import com.jdiazcano.cfg4k.yaml.YamlConfigLoader
import io.kotlintest.data.forall
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AbstractStringSpec
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table

private val providers = jsonProviders() + hoconProviders() + yamlProviders()

class FullConfigProviderTest: StringSpec({
    testProviders("get int") { provider ->
        provider.load("intProperty") shouldBe 1.toConfig()
        provider.get<Int>("intProperty") shouldBe 1
        provider.get("intPropertyWithDefault", 2) shouldBe 2
        provider.getOrNull<Int?>("intPropertyNotExist").shouldBeNull()
        provider.getOrNull("intPropertyWithDefaultOrNull", 2) shouldBe 2

        shouldThrow<SettingNotFound> {
            provider.get<Int>("intPropertyNotExist")
        }

    }

    testProviders("get string") { provider ->
        provider.load("stringProperty") shouldBe "a".toConfig()
        provider.get<String>("stringProperty") shouldBe "a"
        provider.getOrNull<String?>("stringPropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<String>("stringPropertyNotExists")
        }
    }

    testProviders("get long") { provider ->
        provider.load("longProperty") shouldBe 1L.toConfig()
        provider.get<Long>("longProperty") shouldBe 1L
        provider.getOrNull<Long?>("longPropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Long>("longPropertyNotExists")
        }
    }

    testProviders("get short") { provider ->
        provider.load("shortProperty") shouldBe 1.toShort().toConfig()
        provider.get<Short>("shortProperty") shouldBe 1.toShort()
        provider.getOrNull<Short?>("shortPropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Short>("shortPropertyNotExists")
        }
    }

    testProviders("get byte") { provider ->
        provider.load("byteProperty") shouldBe 1.toByte().toConfig()
        provider.get<Byte>("byteProperty") shouldBe 1.toByte()
        provider.getOrNull<Byte?>("bytePropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Byte>("bytePropertyNotExists")
        }
    }

    testProviders("get double") { provider ->
        provider.load("doubleProperty") shouldBe 1.1.toConfig()
        provider.get<Double>("doubleProperty") shouldBe 1.1
        provider.getOrNull<Double?>("doublePropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Double>("doublePropertyNotExists")
        }
    }

    testProviders("get float") { provider ->
        provider.load("floatProperty") shouldBe 1.1F.toConfig()
        provider.get<Float>("floatProperty") shouldBe 1.1F
        provider.getOrNull<Float?>("floatPropertyNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Float>("floatPropertyNotExists")
        }
    }

    testProviders("get a potato") { provider ->
        provider.load("potato") shouldBe MapConfigObject(mapOf("name" to "PotatoName".toConfig(), "size" to 1.toConfig()))
        provider.get<Potato>("potato") shouldBe Potato("PotatoName", 1)
        provider.getOrNull<Potato?>("potatoNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Potato>("potatoNotExists")
        }
    }

    testProviders("get a potato list") { provider ->
        provider.get<List<Potato>>("potatoList") shouldBe listOf(Potato("PotatoName", 1))
        provider.getOrNull<List<Potato>?>("potatoListNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<List<Potato>>("potatoListNotExists")
        }
    }

    testProviders("get a potato map") { provider ->
        provider.get<Map<String, Potato>>("potatoMap") shouldBe mapOf("1" to Potato("PotatoName1", 1), "2" to Potato("PotatoName2", 2))
        provider.getOrNull<Map<String, Potato>?>("potatoMapNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Map<String, Potato>>("potatoMapNotExists")
        }
    }

    testProviders("get a potato map with int key") { provider ->
        provider.reload()

        provider.get<Map<Int, Potato>>("potatoMap") shouldBe mapOf(1 to Potato("PotatoName1", 1), 2 to Potato("PotatoName2", 2))
        provider.getOrNull<Map<Int, Potato>?>("potatoMapNotExists").shouldBeNull()

        shouldThrow<SettingNotFound> {
            provider.get<Map<Int, Potato>>("potatoMapNotExists")
        }
    }

    testProviders("binding with a data class") { provider ->
        provider.reload()
        val all = provider.bind<ObjectWithAllTheThings>("")
        all.apply {
            intProperty shouldBe 1
            stringProperty shouldBe "a"
            longProperty shouldBe 1L
            shortProperty shouldBe 1.toShort()
            byteProperty shouldBe 1.toByte()
            doubleProperty shouldBe 1.1
            floatProperty shouldBe 1.1F
            potato shouldBe Potato("PotatoName", 1)
            potatoList shouldBe listOf(Potato("PotatoName", 1))
            potatoMap shouldBe mapOf("1" to Potato("PotatoName1", 1), "2" to Potato("PotatoName2", 2))
            randomThing shouldBe null
            nullableString shouldBe "a"
//            stringWithDefault shouldBe "def"
        }
    }

    testProviders("binding with a interface and property") { provider ->
        provider.reload()
        val all = provider.bind<InterfacePropertyWithAllTheThings>("")
        all.apply {
            intProperty shouldBe 1
            stringProperty shouldBe "a"
            longProperty shouldBe 1L
            shortProperty shouldBe 1.toShort()
            byteProperty shouldBe 1.toByte()
            doubleProperty shouldBe 1.1
            floatProperty shouldBe 1.1F
            potato shouldBe Potato("PotatoName", 1)
            potatoList shouldBe listOf(Potato("PotatoName", 1))
            potatoMap shouldBe mapOf("1" to Potato("PotatoName1", 1), "2" to Potato("PotatoName2", 2))
            randomThing shouldBe null
            nullableString shouldBe "a"
        }
    }

    testProviders("binding with a interface y method") { provider ->
        provider.reload()
        val all = provider.bind<InterfaceMethodWithAllTheThings>("")
        all.apply {
            intProperty() shouldBe 1
            stringProperty() shouldBe "a"
            longProperty() shouldBe 1L
            shortProperty() shouldBe 1.toShort()
            byteProperty() shouldBe 1.toByte()
            doubleProperty() shouldBe 1.1
            floatProperty() shouldBe 1.1F
            potato() shouldBe Potato("PotatoName", 1)
            potatoList() shouldBe listOf(Potato("PotatoName", 1))
            potatoMap() shouldBe mapOf("1" to Potato("PotatoName1", 1), "2" to Potato("PotatoName2", 2))
            randomThing() shouldBe null
            nullableString() shouldBe "a"
            stringWithDefault() shouldBe "def"
        }
    }

})

private fun AbstractStringSpec.testProviders(testName: String, method: (ConfigProvider) -> Unit) {
    testName {
        forall(*(providers.map { row(it) }.toTypedArray())) { provider ->
            method(provider)
        }
    }
}

fun jsonProviders(): List<ConfigProvider> {
    val source = StringConfigSource("""
        {
          "intProperty": 1,
          "stringProperty": "a",
          "longProperty": 1,
          "shortProperty": 1,
          "byteProperty": 1,
          "doubleProperty": 1.1,
          "floatProperty": 1.1,
          "nullableString": "a",
          "potato": {
            "name": "PotatoName",
            "size": 1
          },
          "potatoList": [
              {
                "name": "PotatoName",
                "size": 1
              }
          ],
          "potatoMap": {
              "1": {
                "name": "PotatoName1",
                "size": 1
              },
              "2": {
                "name": "PotatoName2",
                "size": 2
              }
          }
        }
    """.trimIndent())
    val loader = JsonConfigLoader(source)
    val provider = DefaultConfigProvider(loader)
    val cached = DefaultConfigProvider(loader).cache()
    val bytebuddy = ByteBuddyConfigProvider(loader)
    val bytebuddyCached = ByteBuddyConfigProvider(loader).cache()

    return listOf(provider, cached, bytebuddy, bytebuddyCached)
}

fun yamlProviders(): List<ConfigProvider> {
    val source = StringConfigSource("""
        intProperty: 1
        stringProperty: "a"
        longProperty: 1
        shortProperty: 1
        byteProperty: 1
        doubleProperty: 1.1
        floatProperty: 1.1
        nullableString: "a"
        potato:
          name: "PotatoName"
          size: 1
        potatoList:
          - name: "PotatoName"
            size: 1
        potatoMap:
          1:
            name: "PotatoName1"
            size: 1
          2:
            name: "PotatoName2"
            size: 2
    """.trimIndent())
    val loader = YamlConfigLoader(source)
    val provider = DefaultConfigProvider(loader)
    val cached = DefaultConfigProvider(loader).cache()
    val bytebuddy = ByteBuddyConfigProvider(loader)
    val bytebuddyCached = ByteBuddyConfigProvider(loader).cache()

    return listOf(provider, cached, bytebuddy, bytebuddyCached)
}

fun hoconProviders(): List<ConfigProvider> {
    val source = StringConfigSource("""
        {
          "intProperty": 1,
          "stringProperty": "a",
          "longProperty": 1,
          "shortProperty": 1,
          "byteProperty": 1,
          "doubleProperty": 1.1,
          "floatProperty": 1.1,
          "nullableString": "a",
          "potato": {
            "name": "PotatoName",
            "size": 1
          },
          "potatoList": [
             {
               "name": "PotatoName",
               "size": 1
             }
          ],
          "potatoMap": {
             "1": {
               "name": "PotatoName1",
               "size": 1
             },
             "2": {
               "name": "PotatoName2",
               "size": 2
             }
          }
        }
    """.trimIndent())
    val loader = HoconConfigLoader(source)
    val provider = DefaultConfigProvider(loader)
    val cached = DefaultConfigProvider(loader).cache()
    val bytebuddy = ByteBuddyConfigProvider(loader)
    val bytebuddyCached = ByteBuddyConfigProvider(loader).cache()

    return listOf(provider, cached, bytebuddy, bytebuddyCached)
}