
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.StringConfigSource
import utils.School

const val schoolJson = """{
      "name": "Nice school",
      "professors": [
        {
          "name": "John",
          "age": 30
        },
        {
          "name": "Alice",
          "age": 30
        }
      ]
    }"""

fun main(args: Array<String>) {
    val source = StringConfigSource(schoolJson)  // 1- Define the source
    val loader = JsonConfigLoader(source)        // 2- Define HOW you want to load it (as Json in this case)
    val provider = DefaultConfigProvider(loader) // 3- Create a provider that will let you get/bind

    val secondProfessorName = provider.get<String>("professors[1].name")
    println("SecondProfessor name: $secondProfessorName")

    val school = provider.bind<School>() // You can omit the binding if there's nothing else in the document

    println("Name: ${school.professors.first().name}")
    println("Age: ${school.professors.first().age}")
}