package com.jdiazcano.cfg4k.utils

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TypeConverterTest : Spek({
    describe("a converter from Type") {
        it("should convert a simple class") {
            typeOf<Int>().convert().should.be.equal(TypeStructure(
                        typeOf<Int>()
            ))
        }

        it("should convert a simple list") {
            typeOf<List<Int>>().convert().should.be.equal(TypeStructure(
                        typeOf<List<Int>>(),
                        arrayListOf(TypeStructure(Integer::class.java))
            ))
        }

        it("should convert list within lists") {
            typeOf<List<List<Int>>>().convert().should.be.equal(TypeStructure(
                    typeOf<List<List<Int>>>(),
                    arrayListOf(TypeStructure(
                            typeOf<List<Int>>(),
                            arrayListOf(TypeStructure(Integer::class.java))
                    ))
            ))
        }

        it("should convert a map") {
            typeOf<Map<List<String>, Int>>().convert().should.be.equal(TypeStructure(
                        typeOf<Map<List<String>, Int>>(),
                        arrayListOf(
                                TypeStructure(typeOf<List<String>>(), arrayListOf(TypeStructure(String::class.java))),
                                TypeStructure(Integer::class.java)
                        )
            ))
        }

        it("should convert nested maps and lists") {
            typeOf<List<Map<List<String>, Int>>>().convert().should.be.equal(TypeStructure(
                    typeOf<List<Map<List<String>, Int>>>(),
                    arrayListOf(TypeStructure(
                            typeOf<Map<List<String>, Int>>(),
                            arrayListOf(
                                    TypeStructure(typeOf<List<String>>(), arrayListOf(TypeStructure(String::class.java))),
                                    TypeStructure(Integer::class.java)
                            )
                    ))
            ))
        }
    }
})