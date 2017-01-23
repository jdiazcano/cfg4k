package com.jdiazcano.konfig

import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.parsers.Parser
import com.jdiazcano.konfig.parsers.Parsers
import com.jdiazcano.konfig.providers.ProxyConfigProvider
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * A classed parser should be alright! The type is passed to the parse function if needed. Right now it is hard to think
 * of an use case different than Enums but at some point someone will face it and here is the test (besides enums) that
 * prove they work!
 */
class ClassedParserTest: Spek({
    describe("A new parser is registered and it should parse it correctly with the new parser") {
        Parsers.addClassedParser(Person::class.java, PrinterClassedParser())

        val loader = PropertyConfigLoader(javaClass.classLoader.getResource("classedparser.properties"))
        val provider = ProxyConfigProvider(loader)

        it("a printer is parsed correctly") {
            provider.getProperty<Printer>("persons.me").print().should.be.equal("Javi, 26")
        }

        it("a list of printers should be parsed too") {
            provider.getProperty<List<Printer>>("persons.all").should.be.equal(listOf(Printer("Javi", 26), Printer("Peter", 20)))
        }
    }
})

class PrinterClassedParser : Parser<Printer> {
    override fun parse(value: String, type: Class<*>, parser: Parser<*>): Printer {
        val split = value.split('-')
        return Printer(split[0], split[1].toInt())
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