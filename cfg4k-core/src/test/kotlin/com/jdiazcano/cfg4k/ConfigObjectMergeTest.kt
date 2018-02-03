package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.core.ListConfigObject
import com.jdiazcano.cfg4k.core.MapConfigObject
import com.jdiazcano.cfg4k.core.toConfig
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.InvalidObjectException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigObjectMergeTest : Spek({
    describe("a config object that can and should be merged") {
        it("merge lists") {
            val list1 = ListConfigObject(listOf(
                    1.toConfig(),
                    2.toConfig()
            ))

            val list2 = ListConfigObject(listOf(
                    3.toConfig(),
                    4.toConfig()
            ))

            val mergedList = list1.merge(list2)
            assertEquals(
                    ListConfigObject(listOf(
                            1.toConfig(),
                            2.toConfig(),
                            3.toConfig(),
                            4.toConfig()
                    )),
                    mergedList
            )
        }

        it("merge maps with collision") {
            val list1 = MapConfigObject(mapOf(
                    "a" to 1.toConfig(),
                    "b" to 2.toConfig()
            ))

            val list2 = MapConfigObject(mapOf(
                    "a" to 3.toConfig(),
                    "d" to 4.toConfig()
            ))

            assertFailsWith<InvalidObjectException> { list1.merge(list2) }
        }

        it("merge lists with collision will just take the object once") {
            val list1 = ListConfigObject(listOf(
                    1.toConfig(),
                    2.toConfig()
            ))

            val list2 = ListConfigObject(listOf(
                    2.toConfig(),
                    4.toConfig()
            ))

            val mergedList = list1.merge(list2)
            assertEquals(
                    ListConfigObject(listOf(
                            1.toConfig(),
                            2.toConfig(),
                            4.toConfig()
                    )),
                    mergedList
            )
        }

        it("merge maps") {
            val list1 = MapConfigObject(mapOf(
                    "a" to 1.toConfig(),
                    "b" to 2.toConfig()
            ))

            val list2 = MapConfigObject(mapOf(
                    "c" to 3.toConfig(),
                    "d" to 4.toConfig()
            ))

            val mergedList = list1.merge(list2)
            assertEquals(
                    MapConfigObject(mapOf(
                            "a" to 1.toConfig(),
                            "b" to 2.toConfig(),
                            "c" to 3.toConfig(),
                            "d" to 4.toConfig()
                    )),
                    mergedList
            )
        }

        it("merge super nested") {
            val list1 = MapConfigObject(mapOf(
                    "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig())),
                    "b" to MapConfigObject(mapOf(
                            "aa" to 11.toConfig(),
                            "ab" to 22.toConfig(),
                            "ac" to 33.toConfig()
                    ))
            ))

            val list2 = MapConfigObject(mapOf(
                    "a" to ListConfigObject(listOf(3.toConfig())),
                    "c" to 3.toConfig(),
                    "d" to 4.toConfig()
            ))

            val mergedList = list1.merge(list2)
            assertEquals(
                    MapConfigObject(mapOf(
                            "a" to ListConfigObject(listOf(1.toConfig(), 2.toConfig(), 3.toConfig())),
                            "b" to MapConfigObject(mapOf(
                                    "aa" to 11.toConfig(),
                                    "ab" to 22.toConfig(),
                                    "ac" to 33.toConfig()
                            )),
                            "c" to 3.toConfig(),
                            "d" to 4.toConfig()
                    )),
                    mergedList
            )
        }
    }
})