package com.jdiazcano.cfg4k.cli

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.JCommander
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.DefaultConfigLoader
import java.util.HashMap

class CommandLineLoader(args: Array<String>) : DefaultConfigLoader() {

    init {
        val parsed = JCommander.newBuilder().args(args).addObject(Args()).build().objects[0] as Args
        root = parsed.params.toConfig()
    }

    override fun reload() {}
}

private open class Args {
    @DynamicParameter(names = arrayOf("-"))
    var params = HashMap<String, String>()
}