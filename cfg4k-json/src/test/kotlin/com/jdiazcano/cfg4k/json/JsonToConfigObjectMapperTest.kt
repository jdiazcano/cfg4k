package com.jdiazcano.cfg4k.json

import com.beust.klaxon.Parser
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.asList
import com.jdiazcano.cfg4k.core.asObject
import com.jdiazcano.cfg4k.core.isList
import com.jdiazcano.cfg4k.core.isObject
import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import java.io.ByteArrayInputStream

class JsonToConfigObjectMapperTest : Spek({
    val parser = Parser()
    val json =
            """{
"int": 1,
"str": "string",
"obj": {
    "objstr": "objstring",
    "objint": 11
},
"listobj": [
    {
        "liststr": "objstring0",
        "listint": 0,
        "objinlist": {
            "test": 0
        }
    },
    {
        "liststr": "objstring1",
        "listint": 1,
        "objinlist": {
            "test": 1
        }
    }
],
"listitems": [ 0, 1, 2, 3, 4 ]
}"""
    describe("a parser that can map to config object") {
        val configObject = parser.asConfigObjectFromJson(ByteArrayInputStream(json.toByteArray()))

        configObject.isObject().should.be.`true`
        configObject.asObject()["int"].should.be.equal(1.toConfig())
        configObject.asObject()["str"].should.be.equal("string".toConfig())
        configObject.asObject()["obj"].should.be.equal(MapConfigObject(
                mapOf("objstr" to "objstring".toConfig(),
                        "objint" to "11".toConfig())
        ))
        val listConfigObject = configObject.asObject()["listobj"]
        listConfigObject.should.not.be.`null`
        listConfigObject?.let {
            it.isList().should.be.`true`
            it.asList().size.should.be.equal(2)
            it.asList().forEachIndexed { index, obj ->
                obj.isObject().should.be.`true`
                obj.asObject()["liststr"].should.be.equal("objstring$index".toConfig())
                obj.asObject()["listint"].should.be.equal(index.toConfig())
                obj.asObject()["objinlist"]!!.isObject().should.be.`true`
                obj.asObject()["objinlist"]!!.asObject()["test"].should.be.equal(index.toConfig())
            }
        }
        val listItemsObject = configObject.asObject()["listitems"]
        listItemsObject!!.isList().should.be.`true`
        listItemsObject.asList().size.should.be.equal(5)
        listItemsObject.asList().forEachIndexed { index, configObject -> configObject.should.be.equal(index.toConfig()) }
    }
})