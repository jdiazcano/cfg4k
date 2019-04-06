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

class ListParserTest: StringSpec({
    val json = """
        {
            "liststr": [ "a", "b" ],
            "listliststr": [ [ "a" ], [ "b" ] ],
            "listint": [ 1, 2 ],
            "listlistint": [ [ 1 ], [ 2 ] ],
            "potatolist": [
                {
                    "name": "Super potato",
                    "size": 1
                }
            ]
        }
        """.trimIndent()
    val loader = JsonConfigLoader(StringConfigSource(json))
    val provider = DefaultConfigProvider(loader)
    val context = ConfigContext(provider, "")

    "can parse a normal list of strings" {
        ListParser.parse(context, loader.get("liststr")!!, listStructure<String>())
                .shouldBe(listOf("a", "b"))
    }

    "can parse a normal list of integers" {
        ListParser.parse(context, loader.get("listint")!!, listStructure<Int>())
                .shouldBe(listOf(1, 2))
    }

    "can parse a normal String to List<String> map" {
        ListParser.parse(context, loader.get("listliststr")!!, listListStructure<String>())
                .shouldBe(listOf(listOf("a"), listOf("b")))
    }

    "can parse a normal String to List<Int> map" {
        ListParser.parse(context, loader.get("listlistint")!!, listListStructure<Int>())
                .shouldBe(listOf(listOf(1), listOf(2)))
    }

    "can parse a normal String to List<Potato> map" {
        ListParser.parse(context.copy(propertyName = "potatolist"), loader.get("potatolist")!!, listStructure<Potato>())
                .shouldBe(listOf(Potato("Super potato", 1)))
    }
})

inline fun <reified TYPE> listStructure(): TypeStructure {
    return TypeStructure(
            typeOf<List<TYPE>>(),
            arrayListOf(
                    TypeStructure(typeOf<TYPE>(), arrayListOf(TypeStructure(TYPE::class.java)))
            )
    )
}

inline fun <reified TYPE> listListStructure(): TypeStructure {
    return TypeStructure(
            typeOf<List<List<TYPE>>>(),
            arrayListOf(
                    TypeStructure(typeOf<List<TYPE>>(), arrayListOf(TypeStructure(TYPE::class.java)))
            )
    )
}