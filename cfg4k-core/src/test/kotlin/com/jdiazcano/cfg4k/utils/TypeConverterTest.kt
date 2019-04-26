package com.jdiazcano.cfg4k.utils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TypeConverterTest: StringSpec({

    "should convert a simple class" {
        typeOf<Int>().convert().shouldBe(TypeStructure(
                typeOf<Int>()
        ))
    }

    "should convert a simple list" {
        typeOf<List<Int>>().convert().shouldBe(TypeStructure(
                typeOf<List<Int>>(),
                arrayListOf(TypeStructure(Integer::class.java))
        ))
    }

    "should convert list within lists" {
        typeOf<List<List<Int>>>().convert().shouldBe(TypeStructure(
                typeOf<List<List<Int>>>(),
                arrayListOf(TypeStructure(
                        typeOf<List<Int>>(),
                        arrayListOf(TypeStructure(Integer::class.java))
                ))
        ))
    }

    "should convert a map" {
        typeOf<Map<List<String>, Int>>().convert().shouldBe(TypeStructure(
                typeOf<Map<List<String>, Int>>(),
                arrayListOf(
                        TypeStructure(typeOf<List<String>>(), arrayListOf(TypeStructure(String::class.java))),
                        TypeStructure(Integer::class.java)
                )
        ))
    }

    "should convert nested maps and lists" {
        typeOf<List<Map<List<String>, Int>>>().convert().shouldBe(TypeStructure(
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

})