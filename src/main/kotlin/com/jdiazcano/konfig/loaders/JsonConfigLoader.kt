package com.jdiazcano.konfig.loaders

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.obj
import com.beust.klaxon.string
import com.jdiazcano.konfig.ConfigLoader
import java.io.InputStream

class JsonConfigLoader(
        inputStream: InputStream
): ConfigLoader {
    val parser = Parser()
    val json = parser.parse(inputStream) as JsonObject

    override fun get(key: String): String {
        if (key.contains('.')) {
            var tempJson: JsonObject? = null
            val splitKey = key.split('.')
            splitKey.dropLast(1).forEach { tempJson = json.obj(it) }
            return tempJson!!.string(splitKey.last())?: ""
        } else {
            return json.string(key)?: ""
        }
    }

}
