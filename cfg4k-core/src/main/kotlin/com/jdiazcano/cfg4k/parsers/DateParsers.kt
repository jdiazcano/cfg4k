package com.jdiazcano.cfg4k.parsers

import com.jdiazcano.cfg4k.core.ConfigObject
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class DateParser(private val format: String, private val locale: Locale = Locale.getDefault()) : Parser<Date> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): Date {
        return SimpleDateFormat(format, locale).parse(value.asString())
    }
}

class LocalDateTimeParser private constructor(
        private val locale: Locale,
        private val zone: ZoneId
) : Parser<LocalDateTime> {
    private var format: String? = null
    private var formatter: DateTimeFormatter? = null

    constructor(
            format: String,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.format = format
    }

    constructor(
            formatter: DateTimeFormatter,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.formatter = formatter
    }

    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): LocalDateTime {
        if (formatter != null) {
            return LocalDateTime.parse(value.asString(), formatter)
        } else {
            return LocalDateTime.parse(value.asString(), DateTimeFormatter.ofPattern(format, locale).withZone(zone))
        }
    }
}

class LocalDateParser private constructor(
        private val locale: Locale,
        private val zone: ZoneId
) : Parser<LocalDate> {
    private var format: String? = null
    private var formatter: DateTimeFormatter? = null

    constructor(
            format: String,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
            ): this(locale, zone) {
        this.format = format
    }

    constructor(
            formatter: DateTimeFormatter,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.formatter = formatter
    }

    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): LocalDate {
        if (formatter != null) {
            return LocalDate.parse(value.asString(), formatter)
        } else {
            return LocalDate.parse(value.asString(), DateTimeFormatter.ofPattern(format, locale).withZone(zone))
        }
    }
}

class ZonedDateTimeParser private constructor(
        private val locale: Locale,
        private val zone: ZoneId
) : Parser<ZonedDateTime> {
    private var format: String? = null
    private var formatter: DateTimeFormatter? = null

    constructor(
            format: String,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.format = format
    }

    constructor(
            formatter: DateTimeFormatter,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.formatter = formatter
    }

    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): ZonedDateTime {
        if (formatter != null) {
            return ZonedDateTime.parse(value.asString(), formatter)
        } else {
            return ZonedDateTime.parse(value.asString(), DateTimeFormatter.ofPattern(format, locale).withZone(zone))
        }
    }
}

class OffsetDateTimeParser private constructor(
        private val locale: Locale,
        private val zone: ZoneId
) : Parser<OffsetDateTime> {
    private var format: String? = null
    private var formatter: DateTimeFormatter? = null

    constructor(
            format: String,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.format = format
    }

    constructor(
            formatter: DateTimeFormatter,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.formatter = formatter
    }

    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): OffsetDateTime {
        if (formatter != null) {
            return OffsetDateTime.parse(value.asString(), formatter)
        } else {
            return OffsetDateTime.parse(value.asString(), DateTimeFormatter.ofPattern(format, locale).withZone(zone))
        }
    }
}

class OffsetTimeParser private constructor(
        private val locale: Locale,
        private val zone: ZoneId
) : Parser<OffsetTime> {
    private var format: String? = null
    private var formatter: DateTimeFormatter? = null

    constructor(
            format: String,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.format = format
    }

    constructor(
            formatter: DateTimeFormatter,
            locale: Locale = Locale.getDefault(),
            zone: ZoneId = ZoneId.systemDefault()
    ): this(locale, zone) {
        this.formatter = formatter
    }

    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): OffsetTime {
        if (formatter != null) {
            return OffsetTime.parse(value.asString(), formatter)
        } else {
            return OffsetTime.parse(value.asString(), DateTimeFormatter.ofPattern(format, locale).withZone(zone))
        }
    }
}

class CalendarParser(private val format: String, private val locale: Locale = Locale.getDefault()) : Parser<Calendar> {
    override fun parse(value: ConfigObject, type: Class<*>, parser: Parser<*>?): Calendar {
        val calendar = Calendar.getInstance(locale)
        calendar.time = SimpleDateFormat(format, locale).parse(value.asString())
        return calendar
    }
}