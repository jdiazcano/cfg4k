package com.jdiazcano.cfg4k.json

import com.jdiazcano.cfg4k.bytebuddy.bytebuddy
import com.jdiazcano.cfg4k.parsers.*
import com.jdiazcano.cfg4k.providers.Providers
import com.jdiazcano.cfg4k.providers.Providers.cached
import com.jdiazcano.cfg4k.providers.Providers.proxy
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.utils.SettingNotFound
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ConfigProviderTest : Spek({

    val providers = listOf(
            Providers.bytebuddy(JsonConfigLoader(javaClass.classLoader.getResource("test.json"))),
            cached(proxy(JsonConfigLoader(javaClass.classLoader.getResource("test.json"))))
    )

    providers.forEachIndexed { i, provider ->
        describe("provider[$i]") {
            it("default values") {
                assertEquals(1, provider.get("this.does.not.exist", 1))
                // When having a cached provider then it will cache the "this.does.not.exist" if it has a default value
                // because the delegated provider will return the default value. Should the default value not be passed
                // and the exception caught? I think that would mean a performance impact and having exceptions into
                // account for normal logic is not right
                assertFailsWith<SettingNotFound> {
                    provider.get<Int>("i.dont.extist")
                }
            }

            it("integer properties") {
                assertEquals(1, provider.get("integerProperty", Int::class.java))
            }

            it("long properties") {
                assertEquals(2, provider.get("longProperty", Long::class.java))
            }

            it("short properties") {
                assertEquals(1, provider.get("shortProperty", Short::class.java))
            }

            it("float properties") {
                assertEquals(2.1F, provider.get("floatProperty", Float::class.java))
            }

            it("double properties") {
                assertEquals(1.1, provider.get("doubleProperty", Double::class.java))
            }

            it("byte properties") {
                assertEquals(2, provider.get("byteProperty", Byte::class.java))
            }

            it("boolean properties") {
                assertTrue(provider.get("booleanProperty", Boolean::class.java))
            }

            it("big integer properties") {
                assertEquals(BigInteger("1"), provider.get<BigInteger>("bigIntegerProperty"))
            }

            it("big decimal properties") {
                assertEquals(BigDecimal("1.1"), provider.get<BigDecimal>("bigDecimalProperty"))
            }

            it("file properties") {
                assertEquals(File("myfile.txt"), provider.get<File>("file"))
            }

            it("path properties") {
                assertEquals(Paths.get("mypath.txt"), provider.get<Path>("path"))
            }

            it("url properties") {
                assertEquals(URL("https://www.amazon.com"), provider.get<URL>("url"))
            }

            it("uri properties") {
                assertEquals(URI("https://www.amazon.com"), provider.get<URI>("uri"))
            }

            it("date property") {
                Parsers.addParser(Date::class.java, DateParser("dd-MM-yyyy"))
                val date = provider.get<Date>("dateProperty")

                // A calendar must be built on top of that date to work with it
                val calendar = Calendar.getInstance()
                calendar.time = date
                assertEquals(1, calendar.get(Calendar.DAY_OF_YEAR))
                assertEquals(0, calendar.get(Calendar.MONTH))
                assertEquals(2017, calendar.get(Calendar.YEAR))
            }

            it("localdateproperty property") {
                Parsers.addParser(LocalDate::class.java, LocalDateParser("dd-MM-yyyy"))
                val localDate = provider.get<LocalDate>("localDateProperty")
                assertEquals(1, localDate.dayOfYear)
                assertEquals(Month.JANUARY, localDate.month)
                assertEquals(2017, localDate.year)
            }

            it("isolocaldateproperty property") {
                Parsers.addParser(LocalDate::class.java, LocalDateParser(DateTimeFormatter.ISO_LOCAL_DATE))
                val localDate = provider.get<LocalDate>("isoLocalDateProperty")
                assertEquals(1, localDate.dayOfYear)
                assertEquals(Month.JANUARY, localDate.month)
                assertEquals(2017, localDate.year)
            }

            it("localdatetime property") {
                Parsers.addParser(LocalDateTime::class.java, LocalDateTimeParser("dd-MM-yyyy HH:mm:ss"))
                val localDateTime = provider.get<LocalDateTime>("localDateTimeProperty")
                assertEquals(1, localDateTime.dayOfYear)
                assertEquals(Month.JANUARY, localDateTime.month)
                assertEquals(2017, localDateTime.year)
                assertEquals(18, localDateTime.hour)
                assertEquals(1, localDateTime.minute)
                assertEquals(31, localDateTime.second)
            }

            it("isolocaldatetime property") {
                Parsers.addParser(LocalDateTime::class.java, LocalDateTimeParser(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                val localDateTime = provider.get<LocalDateTime>("isoLocalDateTimeProperty")
                assertEquals(1, localDateTime.dayOfYear)
                assertEquals(Month.JANUARY, localDateTime.month)
                assertEquals(2017, localDateTime.year)
                assertEquals(18, localDateTime.hour)
                assertEquals(1, localDateTime.minute)
                assertEquals(31, localDateTime.second)
            }

            it("zoneddatetime property") {
                Parsers.addParser(ZonedDateTime::class.java, ZonedDateTimeParser("dd-MM-yyyy HH:mm:ss"))
                val zonedDateTime = provider.get<ZonedDateTime>("zonedDateTimeProperty")
                assertEquals(1, zonedDateTime.dayOfYear)
                assertEquals(Month.JANUARY, zonedDateTime.month)
                assertEquals(2017, zonedDateTime.year)
                assertEquals(18, zonedDateTime.hour)
                assertEquals(1, zonedDateTime.minute)
                assertEquals(31, zonedDateTime.second)
            }

            it("isozoneddatetime property") {
                Parsers.addParser(ZonedDateTime::class.java, ZonedDateTimeParser(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                val zonedDateTime = provider.get<ZonedDateTime>("isoZonedDateTimeProperty")
                assertEquals(1, zonedDateTime.dayOfYear)
                assertEquals(Month.JANUARY, zonedDateTime.month)
                assertEquals(2017, zonedDateTime.year)
                assertEquals(18, zonedDateTime.hour)
                assertEquals(1, zonedDateTime.minute)
                assertEquals(31, zonedDateTime.second)
            }

            it("offsetdatetime property") {
                Parsers.addParser(OffsetDateTime::class.java, OffsetDateTimeParser("dd-MM-yyyy HH:mm:ssXXX"))
                val offsetDateTime = provider.get<OffsetDateTime>("offsetDateTimeProperty")
                assertEquals(1, offsetDateTime.dayOfYear)
                assertEquals(Month.JANUARY, offsetDateTime.month)
                assertEquals(2017, offsetDateTime.year)
                assertEquals(18, offsetDateTime.hour)
                assertEquals(1, offsetDateTime.minute)
                assertEquals(31, offsetDateTime.second)
            }

            it("isooffsetdatetime property") {
                Parsers.addParser(OffsetDateTime::class.java, OffsetDateTimeParser(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                val offsetDateTime = provider.get<OffsetDateTime>("isoOffsetDateTimeProperty")
                assertEquals(1, offsetDateTime.dayOfYear)
                assertEquals(Month.JANUARY, offsetDateTime.month)
                assertEquals(2017, offsetDateTime.year)
                assertEquals(18, offsetDateTime.hour)
                assertEquals(1, offsetDateTime.minute)
                assertEquals(31, offsetDateTime.second)
            }

            it("offsettime property") {
                Parsers.addParser(OffsetTime::class.java, OffsetTimeParser("HH:mm:ssXXX"))
                val offsetTime = provider.get<OffsetTime>("offsetTimeProperty")
                assertEquals(18, offsetTime.hour)
                assertEquals(1, offsetTime.minute)
                assertEquals(31, offsetTime.second)
            }

            it("isooffsettime property") {
                Parsers.addParser(OffsetTime::class.java, OffsetTimeParser(DateTimeFormatter.ISO_OFFSET_TIME))
                val offsetTime = provider.get<OffsetTime>("isoOffsetTimeProperty")
                assertEquals(18, offsetTime.hour)
                assertEquals(1, offsetTime.minute)
                assertEquals(31, offsetTime.second)
            }

            it("calendar property") {
                Parsers.addParser(Calendar::class.java, CalendarParser("dd-MM-yyyy"))
                val calendar = provider.get<Calendar>("calendarProperty")
                assertEquals(1, calendar.get(Calendar.DAY_OF_YEAR))
                assertEquals(0, calendar.get(Calendar.MONTH))
                assertEquals(2017, calendar.get(Calendar.YEAR))
            }

            it("binding test") {
                val testBinder = provider.bind<TestBinder>("")
                assertTrue(testBinder.booleanProperty())
                assertEquals(1, testBinder.integerProperty())
                assertEquals(2, testBinder.longProperty())
                assertEquals(1, testBinder.shortProperty())
                assertEquals(2.1F, testBinder.floatProperty())
                assertEquals(1.1, testBinder.doubleProperty())
                assertEquals(2, testBinder.byteProperty())
                assertEquals("b", testBinder.a())
                assertEquals("d", testBinder.c())
                assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), testBinder.list())
                assertEquals(listOf(1.2F, 2.2F, 3.2F), testBinder.floatList())

                val doges = listOf(createDoge(0), createDoge(1))
                testBinder.complexSet().forEachIndexed { index, item ->
                    assertEquals(doges[index].doge(), item.doge())
                    assertEquals(doges[index].wow(), item.wow())
                }

                assertEquals(BigDecimal("1.1"), testBinder.bigDecimalProperty())
                assertEquals(BigInteger("1"), testBinder.bigIntegerProperty())
                assertEquals(URI("https://www.amazon.com"), testBinder.uri())
                assertEquals(URL("https://www.amazon.com"), testBinder.url())
                assertEquals(File("myfile.txt"), testBinder.file())
                assertEquals(Paths.get("mypath.txt"), testBinder.path())

                // toString should be the object tostring not the one that comes from the property
                assertNotEquals("this should not be ever used", testBinder.toString())
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
    fun complexSet(): Set<Doge>
    fun bigIntegerProperty(): BigInteger
    fun bigDecimalProperty(): BigDecimal
    fun uri(): URI
    fun url(): URL
    fun file(): File
    fun path(): Path
}

interface Doge {
    fun wow(): String
    fun doge(): Int
}

private fun createDoge(index: Int): Doge {
    return object : Doge {
        override fun wow() = "such$index"

        override fun doge() = index

    }
}