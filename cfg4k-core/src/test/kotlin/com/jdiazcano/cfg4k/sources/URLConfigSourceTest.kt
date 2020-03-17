package com.jdiazcano.cfg4k.sources

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import io.kotlintest.specs.StringSpec
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.net.URL
import java.util.concurrent.TimeUnit

private const val AUTH_HEADER = "Authorization"

class URLConfigSourceTest: FeatureSpec({

    feature("an url config source") {
        scenario("the stream should not be null") {
            val source = URLConfigSource(URLConfigSourceTest::class.java.getResource("/test.properties"))
            source.read().shouldNotBeNull()
        }
    }

    feature("config source fetching data from a URL") {
        val configSource = "a: b"
        val configPath = "/path"
        val server = MockWebServer().apply { start() }
        val configUrl = server.url(configPath).toUrl()

        scenario("should fetch config") {
            val source = URLConfigSource(configUrl)
            server.enqueue(MockResponse().setBody(configSource))

            source.read().bufferedReader().useLines {
                val fetchedConfigSource = it.toList().single()
                fetchedConfigSource.shouldBe(configSource)

                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)!!
                request.method shouldBe "GET"
                request.path shouldBe configPath
            }
        }

        scenario("should fetch config without authentication") {
            val source = URLConfigSource(configUrl)
            server.enqueue(MockResponse().setBody(configSource))
            // Calling use to auto-close the stream
            source.read().use {
                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)!!
                request.getHeader(AUTH_HEADER).shouldBeNull()
            }
        }

        scenario("should fetch config with provided authentication header") {
            val authHeader = "some-header"
            val source = URLConfigSource(configUrl, authHeader)
            server.enqueue(MockResponse().setBody(configSource))
            // Calling use to auto-close the stream
            source.read().use {
                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)!!
                request.getHeader(AUTH_HEADER).shouldBe(authHeader)
            }
        }

        server.shutdown()

    }

    feature("a github config source") {
        scenario("should fetch from a public repo") {
            val configSource = GitHubConfigSource("jdiazcano", "cfg4k", "cfg4k-core/src/test/resources/test.properties")
            val provider = DefaultConfigProvider(PropertyConfigLoader(configSource))
            provider.get<String>("a").shouldBe("b")
        }
    }

    feature("authentication header generators") {
        scenario("basic authentication generator") {
            basicAuth("", "").shouldBe("Basic Og==")
            basicAuth("foo", "bar").shouldBe("Basic Zm9vOmJhcg==")
            basicAuth("username", "password").shouldBe("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
        }
    }

    feature("token header generators") {
        scenario("token authentication generator") {
            "mycooltoken".asToken().shouldBe("Token mycooltoken")
        }
    }

    feature("bitbucket config source") {
        scenario("the url is the one expected") {
            val source = BitbucketConfigSource("owner", "slug", "path/config.json") as URLConfigSource
            source.url.toString() shouldBe "https://api.bitbucket.org/2.0/repositories/owner/slug/src/master/path/config.json"
        }

        scenario("the url is the one expected with username and pass") {
            val source = BitbucketUserConfigSource("user", "password","owner", "slug", "path/config.json") as URLConfigSource
            source.url.toString() shouldBe "https://api.bitbucket.org/2.0/repositories/owner/slug/src/master/path/config.json"
        }
    }

    feature("github config source") {
        scenario("the url is the one expected") {
            val source = GitHubConfigSource("owner", "slug", "path/config.json") as URLConfigSource
            source.url.toString() shouldBe "https://raw.githubusercontent.com/owner/slug/master/path/config.json"
        }
    }

})