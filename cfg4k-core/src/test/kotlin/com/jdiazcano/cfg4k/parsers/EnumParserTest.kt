package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.utils.TypeStructure
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk

class EnumParserTest: StringSpec({
    val context = mockk<ConfigContext>()

    "Parses an enum correctly" {
        EnumParser<EnumType>().parse(context, "TYPE".toConfig(), TypeStructure(EnumType::class.java))
                .shouldBe(EnumType.TYPE)
    }

})

enum class EnumType {
    TYPE
    ;
}