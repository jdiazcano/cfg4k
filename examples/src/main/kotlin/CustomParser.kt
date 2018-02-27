
import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.parsers.Parser
import com.jdiazcano.cfg4k.parsers.Parsers
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.StringConfigSource

data class Point(val x: Int, val y: Int)

object PointParser: Parser<Point> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?) = Point(
            value.asString().split(',')[0].toInt(),
            value.asString().split(',')[1].toInt()
    )
}

const val listOfPoints = """[
    "1,2",
    "2,2",
    "3,2"
]"""

/**
 * With the class and parser alternative you have a cool way of printing/comparing as
 * you can have data classes which will generate the toString and equals/hashcode for you.
 */
fun main(args: Array<String>) {
    Parsers.addParser(Point::class.java, PointParser)

    val source = StringConfigSource(listOfPoints)
    val loader = JsonConfigLoader(source)
    val provider = DefaultConfigProvider(loader)

    provider.get<List<Point>>().forEach {
        println(it)
    }
}