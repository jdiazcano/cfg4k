package com.jdiazcano.cfg4k.sources

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class URLConfigSource(private val url: URL,
                      private val authHeader: String? = null) : ConfigSource {
    override fun read(): InputStream {
        val connection = url.openConnection()
        if (connection is HttpURLConnection && authHeader != null) {
            connection.setRequestProperty("Authorization", authHeader)
        }

        return connection.inputStream
    }
}

/**
 * Generates `Authorization` header value for basic authentication with the given [username] and [password].
 */
fun basicAuth(username: String, password: String): String {
    val credentials = "$username:$password"
    val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))
    return "Basic $encodedCredentials"
}
