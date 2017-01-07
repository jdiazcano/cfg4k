import com.jdiazcano.konfig.bind
import com.jdiazcano.konfig.hocon.HoconConfigLoader
import com.jdiazcano.konfig.providers.Providers
import com.typesafe.config.ConfigFactory
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class HoconConfigLoaderTest: Spek({

    val loaders = listOf(
            HoconConfigLoader(File(javaClass.classLoader.getResource("test.properties").toURI())),
            HoconConfigLoader("test.properties"),
            HoconConfigLoader(javaClass.classLoader.getResource("test.properties")),
            HoconConfigLoader(ConfigFactory.parseResources("test.properties"), {ConfigFactory.parseResources("test.properties")})
    )

    loaders.forEachIndexed { i, loader ->
        describe("loader[$i]") {
            it("loader properties") {
                loader.get("integerProperty").should.be.equal("1")
                loader.get("longProperty").should.be.equal("2")
                loader.get("shortProperty").should.be.equal("1")
                loader.get("floatProperty").should.be.equal("2.1")
                loader.get("doubleProperty").should.be.equal("1.1")
                loader.get("byteProperty").should.be.equal("2")
                loader.get("booleanProperty").should.be.equal("true")
            }

            it("works with binding") {
                val provider = Providers.proxy(loader)

                val testBinder = provider.bind<TestBinder>("")
                testBinder.booleanProperty().should.be.`true`
                testBinder.integerProperty().should.be.equal(1)
                testBinder.longProperty().should.be.equal(2)
                testBinder.shortProperty().should.be.equal(1)
                testBinder.floatProperty().should.be.equal(2.1F)
                testBinder.doubleProperty().should.be.equal(1.1)
                testBinder.byteProperty().should.be.equal(2)
                testBinder.a().should.be.equal("b")
                testBinder.c().should.be.equal("d")
                testBinder.list().should.be.equal(listOf(1, 2, 3))
                testBinder.floatList().should.be.equal(listOf(1.2F, 2.2F, 3.2F))

                // toString should be the object tostring not the one that comes from the property
                testBinder.toString().should.not.be.equal("this should not be ever used")
            }
        }
    }

})

interface TestBinder {
    fun integerProperty(): Int
    fun a(): String
    fun c(): String
    fun booleanProperty(): Boolean
    fun longProperty(): Long
    fun shortProperty(): Short
    fun doubleProperty(): Double
    fun floatProperty(): Float
    fun byteProperty(): Byte
    fun list(): List<Int>
    fun floatList(): List<Float>
}
