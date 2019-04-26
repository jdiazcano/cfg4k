package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigContext
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.EnvironmentConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

class DateParsersTest: StringSpec({
    val emptyContext = ConfigContext(DefaultConfigProvider(EnvironmentConfigLoader()), "")

    "DateParser parses it properly" {
        DateParser("dd-MM-yyyy").parse(emptyContext, "01-01-2017".toConfig()).let {
            // A calendar must be built on top of that date to work with it
            val calendar = Calendar.getInstance()
            calendar.time = it
            calendar.get(Calendar.DAY_OF_YEAR).shouldBe(1)
            calendar.get(Calendar.MONTH).shouldBe(0)
            calendar.get(Calendar.YEAR).shouldBe(2017)
        }
    }

    "LocalDateTimeParser parses it properly" {
        LocalDateTimeParser("dd-MM-yyyy HH:mm:ss").parse(emptyContext, "01-01-2017 18:01:31".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "LocalDateTimeParser parses it properly in ISO format" {
        LocalDateTimeParser(DateTimeFormatter.ISO_LOCAL_DATE_TIME).parse(emptyContext, "2017-01-01T18:01:31".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "LocalDateParser parses it properly" {
        LocalDateParser("dd-MM-yyyy").parse(emptyContext, "01-01-2017".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
        }
    }

    "LocalDateParser parses it properly in ISO format" {
        LocalDateParser(DateTimeFormatter.ISO_LOCAL_DATE).parse(emptyContext, "2017-01-01".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
        }
    }

    "ZonedDateTimeParser parses it properly" {
        ZonedDateTimeParser("dd-MM-yyyy HH:mm:ss").parse(emptyContext, "01-01-2017 18:01:31".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "ZonedDateTimeParser parses it properly in ISO format" {
        ZonedDateTimeParser(DateTimeFormatter.ISO_ZONED_DATE_TIME).parse(emptyContext, "2017-01-01T18:01:31+01:00".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "OffsetDateTimeParser parses it properly" {
        OffsetDateTimeParser("dd-MM-yyyy HH:mm:ssXXX").parse(emptyContext, "01-01-2017 18:01:31+01:00".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "OffsetDateTimeParser parses it properly in ISO format" {
        OffsetDateTimeParser(DateTimeFormatter.ISO_OFFSET_DATE_TIME).parse(emptyContext, "2017-01-01T18:01:31+01:00".toConfig()).let {
            it.dayOfYear.shouldBe(1)
            it.month.shouldBe(Month.JANUARY)
            it.year.shouldBe(2017)
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "OffsetTimeParser parses it properly" {
        OffsetTimeParser("HH:mm:ssXXX").parse(emptyContext, "18:01:31+01:00".toConfig()).let {
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "OffsetTimeParser parses it properly in ISO format" {
        OffsetTimeParser(DateTimeFormatter.ISO_OFFSET_TIME).parse(emptyContext, "18:01:31+01:00".toConfig()).let {
            it.hour.shouldBe(18)
            it.minute.shouldBe(1)
            it.second.shouldBe(31)
        }
    }

    "CalendarParser parses it properly" {
        CalendarParser("dd-MM-yyyy").parse(emptyContext, "01-01-2017".toConfig()).let {
            it.get(Calendar.DAY_OF_YEAR).shouldBe(1)
            it.get(Calendar.MONTH).shouldBe(0)
            it.get(Calendar.YEAR).shouldBe(2017)
        }
    }

})