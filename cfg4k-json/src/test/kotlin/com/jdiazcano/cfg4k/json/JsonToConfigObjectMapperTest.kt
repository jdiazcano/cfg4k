package com.jdiazcano.cfg4k.json

import com.beust.klaxon.Parser
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.core.ConfigObjectType
import com.jdiazcano.cfg4k.core.toConfig
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class JsonToConfigObjectMapperTest: Spek({
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
        val configObject = parser.asConfigObjectFromJson(json)

        configObject.type.should.be.equal(ConfigObjectType.OBJECT)
        configObject.properties["int"].should.be.equal(1.toConfig())
        configObject.properties["str"].should.be.equal("string".toConfig())
        configObject.properties["obj"].should.be.equal(ConfigObject(
                mapOf("objstr" to "objstring".toConfig(),
                        "objint" to "11".toConfig())
        ))
        val listConfigObject = configObject.properties["listobj"]
        listConfigObject.should.not.be.`null`
        listConfigObject?.let {
            it.type.should.be.equal(ConfigObjectType.ARRAY)
            it.list.size.should.be.equal(2)
            it.list.forEachIndexed { index, obj ->
                obj.type.should.be.equal(ConfigObjectType.OBJECT)
                obj.properties["liststr"].should.be.equal("objstring$index".toConfig())
                obj.properties["listint"].should.be.equal(index.toConfig())
                obj.properties["objinlist"]!!.type.should.be.equal(ConfigObjectType.OBJECT)
                obj.properties["objinlist"]!!.properties["test"].should.be.equal(index.toConfig())
            }
        }
        val listItemsObject = configObject.properties["listitems"]
        listItemsObject!!.type.should.be.equal(ConfigObjectType.ARRAY)
        listItemsObject.list.size.should.be.equal(5)
        listItemsObject.list.forEachIndexed { index, configObject -> configObject.should.be.equal(index.toConfig()) }
    }
})