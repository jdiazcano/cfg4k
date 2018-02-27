/*
 * Copyright 2015-2016 Javier Díaz-Cano Martín-Albo (javierdiazcanom@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.sources.GitHubConfigSource
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.jdiazcano.cfg4k.sources.asToken
import com.jdiazcano.cfg4k.sources.basicAuth
import com.winterbe.expekt.should
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URL
import java.util.concurrent.TimeUnit

private const val AUTH_HEADER = "Authorization"

class URLConfigSourceTest : Spek({
    describe("config source fetching data from a URL") {
        val configSource = "a: b"
        val configPath = "/path"
        lateinit var server: MockWebServer
        lateinit var configUrl: URL

        beforeEachTest {
            server = MockWebServer()
            server.start()
            configUrl = server.url(configPath).url()
            server.enqueue(MockResponse().setBody(configSource))
        }
        afterEachTest {
            server.shutdown()
        }

        it("should fetch config") {
            val source = URLConfigSource(configUrl)

            source.read().bufferedReader().useLines {
                val fetchedConfigSource = it.toList().single()
                fetchedConfigSource.should.be.equal(configSource)

                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)
                request.method.should.be.equal("GET")
                request.path.should.be.equal(configPath)
            }
        }

        it("should fetch config without authentication") {
            val source = URLConfigSource(configUrl)
            // Calling use to auto-close the stream
            source.read().use {
                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)
                request.getHeader(AUTH_HEADER).should.be.`null`
            }
        }

        it("should fetch config with provided authentication header") {
            val authHeader = "some-header"
            val source = URLConfigSource(configUrl, authHeader)
            // Calling use to auto-close the stream
            source.read().use {
                val request = server.takeRequest(1, TimeUnit.MILLISECONDS)
                request.getHeader(AUTH_HEADER).should.be.equal(authHeader)
            }
        }

    }

    describe("a github config source") {
        it("should fetch from a public repo") {
            val configSource = GitHubConfigSource("jdiazcano", "cfg4k", "cfg4k-core/src/test/resources/test.properties")
            val provider = DefaultConfigProvider(PropertyConfigLoader(configSource))
            provider.get<String>("a").should.be.equal("b")
        }
    }

    describe("authentication header generators") {
        it("basic authentication generator") {
            basicAuth("", "").should.be.equal("Basic Og==")
            basicAuth("foo", "bar").should.be.equal("Basic Zm9vOmJhcg==")
            basicAuth("username", "password").should.be.equal("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
        }
    }

    describe("token header generators") {
        it("token authentication generator") {
            "mycooltoken".asToken().should.be.equal("Token mycooltoken")
        }
    }
})
