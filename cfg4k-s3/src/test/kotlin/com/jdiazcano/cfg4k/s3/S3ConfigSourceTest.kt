package com.jdiazcano.cfg4k.s3

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.jdiazcano.cfg4k.json.JsonConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.cache
import com.jdiazcano.cfg4k.providers.get
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class S3ConfigSourceTest : Spek({
    describe("a config source that can fetch data") {
        val client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
        val source = S3ConfigSource(client, "mtln-public-data", "Samples/airports.json")
        val loader = JsonConfigLoader(source)
        val provider = DefaultConfigProvider(loader).cache()
        val airports = provider.get<List<Airport>>()
        airports.should.not.be.empty
    }
})

private interface Airport {
    val iata: String
    val airport: String
    val city: String
    val state: String
    val country: String
    val lat: Double
    val long: Double
}