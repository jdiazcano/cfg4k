package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.parsers.Parser
import com.jdiazcano.cfg4k.parsers.Parsers
import com.jdiazcano.cfg4k.providers.ProxyConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * A classed parser should be alright! The type is passed to the parse function if needed. Right now it is hard to think
 * of an use case different than Enums but at some point someone will face it and here is the test (besides enums) that
 * prove they work!
 */
class ClassedParserTest : Spek({
    describe("A new parser is registered and it should parse it correctly with the new parser") {
        Parsers.addParser(Person::class.java, PrinterClassedParser())

        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("classedparser.properties"))
        val provider = ProxyConfigProvider(loader)

        it("a printer is parsed correctly") {
            provider.get<Printer>("persons.me").should.be.equal(Printer("Javi", 26))
        }
    }
})

class PrinterClassedParser : Parser<Printer> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): Printer {
        return Printer(value.asObject()["name"]!!.asString(), value.asObject()["age"]!!.asString().toInt())
    }
}

class Printer(name: String, age: Int) : Person(name, age) {
    fun print() = super.toString()
}

open class Person(val name: String, val age: Int) {
    override fun toString() = "$name, $age"

    // Override and equals needed for comparing in tests
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Person

        if (name != other.name) return false
        if (age != other.age) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age
        return result
    }

}