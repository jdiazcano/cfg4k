package com.jdiazcano.konfig

import com.jdiazcano.konfig.utils.typeOf
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TypableTests : Spek({
    describe("Testing the toString of Typable") {
        it("Typable: typeOf<> toString should return its ") {
            typeOf<List<String>>().toString().should.be.equal("GenericType { type: java.util.List<? extends java.lang.String> }")
            typeOf<List<Int>>().toString().should.be.equal("GenericType { type: java.util.List<? extends java.lang.Integer> }")
            typeOf<Int>().toString().should.be.equal("GenericType { type: class java.lang.Integer }")
            typeOf<Map<String, List<Int>>>().toString().should.be.equal("GenericType { type: java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.Integer>> }")
        }
    }
})