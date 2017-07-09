package com.jdiazcano.cfg4k.json

import com.jdiazcano.cfg4k.providers.ProxyConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.utils.typeOf
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class JsonConfigLoaderTest: Spek({
    val loader = JsonConfigLoader(javaClass.classLoader.getResource("test.json"))
    val provider = ProxyConfigProvider(loader)
    describe("a json config loader") {
        it("a value should be b") {
            loader.get("a").should.be.equal("b")
            loader.get("nested.a").should.be.equal("nestedb")
        }
    }

    it("Simple property test") {
        val testBinder: List<Int> = provider.get("list", typeOf<List<Int>>())
        testBinder.should.be.equal(listOf(1, 2, 3, 4, 5, 6, 7))
        val betterIntList: List<Int> = provider.get("betterIntList", typeOf<List<Int>>())
        betterIntList.should.be.equal(listOf(1, 2, 100))
        val betterStringList: List<String> = provider.get("betterStringList", typeOf<List<String>>())
        betterStringList.should.be.equal(listOf("a", "b", "c"))
        val betterEnumList = provider.get<List<Enumerito>>("betterEnumList")
        betterEnumList.should.be.equal(listOf(Enumerito.A, Enumerito.B))
    }
})