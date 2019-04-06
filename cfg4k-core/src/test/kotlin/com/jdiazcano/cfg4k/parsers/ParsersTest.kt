package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.parsers.Parsers.findParser
import com.jdiazcano.cfg4k.parsers.Parsers.isExtendedParseable
import com.jdiazcano.cfg4k.utils.ParserClassNotFound
import com.jdiazcano.cfg4k.utils.TypeStructure
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import kotlin.test.assertFailsWith

class ParsersTest: StringSpec({

    "throws exception if parser isn't found" {
        shouldThrow<ParserClassNotFound> {
            StringSpec::class.java.findParser()
        }
    }

    "can find an enum parser" {
        EnumType::class.java.findParser().shouldNotBeNull()
    }

    "can find the parser for a parseable class" {
        Int::class.java.findParser().shouldNotBeNull()
    }

    "extended parseable returns true for maps" {
        Map::class.java.isExtendedParseable().shouldBeTrue()
    }

    "extended parseable returns true for lists" {
        List::class.java.isExtendedParseable().shouldBeTrue()
    }

    "extended parseable returns true for collection" {
        Collection::class.java.isExtendedParseable().shouldBeTrue()
    }

    "adding a parser will make it parseable" {
        shouldThrow<ParserClassNotFound> {
            ParsersTest::class.java.findParser()
        }

        Parsers.addParser(ParsersTest::class.java, ParsersTestParser)

        ParsersTest::class.java.let {
            it.findParser().shouldNotBeNull()
            it.isExtendedParseable().shouldBeTrue()
        }
    }

})

object ParsersTestParser: Parser<ParsersTest> {
    override fun parse(context: ConfigContext, value: ConfigObject, typeStructure: TypeStructure): ParsersTest {
        throw IllegalStateException()
    }
}