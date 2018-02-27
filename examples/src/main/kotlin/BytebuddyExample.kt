
import com.jdiazcano.cfg4k.bytebuddy.ByteBuddyBinder
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.StringConfigSource
import utils.School

fun main(args: Array<String>) {
    val source = StringConfigSource(schoolJson)
    val loader = JsonConfigLoader(source)
    val provider =  DefaultConfigProvider(loader, binder = ByteBuddyBinder())

    val secondProfessorName = provider.get<String>("professors[1].name")
    println("SecondProfessor name: $secondProfessorName")

    val school = provider.bind<School>() // Here we will have a compiled class instead of a Proxy

    println(school) // We don't get an InvocationHandler

    println("Name: ${school.professors.first().name}")
    println("Age: ${school.professors.first().age}")
}