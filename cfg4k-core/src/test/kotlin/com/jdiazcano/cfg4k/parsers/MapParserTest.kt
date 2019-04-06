package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.Potato
import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.sources.StringConfigSource
import com.jdiazcano.cfg4k.utils.TypeStructure
import com.jdiazcano.cfg4k.utils.typeOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class MapParserTest: StringSpec({
    val json = """
        {
            "str": {
                "str": "b"
            },
            "liststr": {
                "liststr": [ "a", "b" ]
            },
            "listint": {
                "listint": [ 1, 2 ]
            },
            "int": {
                "int": 1
            },
            "potatolist": {
                "potatolist": [
                    {
                        "name": "Super potato",
                        "size": 1
                    }
                ]
            }
        }
        """.trimIndent()
    val loader = JsonConfigLoader(StringConfigSource(json))
    val provider = DefaultConfigProvider(loader)
    val context = ConfigContext(provider, "")

    "can parse a normal String to String map" {
        MapParser.parse(context, loader.get("str")!!, mapStructure<String, String>())
                .shouldBe(mapOf("str" to "b"))
    }

    "can parse a normal String to Integer map" {
        MapParser.parse(context, loader.get("int")!!, mapStructure<String, Int>())
                .shouldBe(mapOf("int" to 1))
    }

    "can parse a normal String to List<String> map" {
        MapParser.parse(context, loader.get("liststr")!!, mapListStructure<String, String>())
                .shouldBe(mapOf("liststr" to listOf("a", "b")))
    }

    "can parse a normal String to List<Int> map" {
        MapParser.parse(context, loader.get("listint")!!, mapListStructure<String, Int>())
                .shouldBe(mapOf("listint" to listOf(1, 2)))
    }

    "can parse a normal String to List<Potato> map" {
        MapParser.parse(context.copy(propertyName = "potatolist"), loader.get("potatolist")!!, mapListStructure<String, Potato>())
                .shouldBe(mapOf("potatolist" to listOf(Potato("Super potato", 1))))
    }
})

inline fun <reified FROM, reified TO> mapStructure(): TypeStructure {
    return TypeStructure(
            typeOf<Map<FROM, TO>>(),
            arrayListOf(
                    TypeStructure(FROM::class.java),
                    TypeStructure(TO::class.java)
            )
    )
}

inline fun <reified FROM, reified TO> mapListStructure(): TypeStructure {
    return TypeStructure(
            typeOf<Map<FROM, TO>>(),
            arrayListOf(
                    TypeStructure(FROM::class.java),
                    TypeStructure(typeOf<List<TO>>(), arrayListOf(TypeStructure(TO::class.java)))
            )
    )
}