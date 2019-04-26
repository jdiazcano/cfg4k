package com.jdiazcano.cfg4k.sources

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class URLConfigSource(val url: URL,
                      private val authHeader: String? = null) : ConfigSource {
    override fun read(): InputStream {
        val connection = url.openConnection()
        if (connection is HttpURLConnection && authHeader != null) {
            connection.setRequestProperty("Authorization", authHeader)
        }

        return connection.inputStream
    }
}


private const val GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com"
fun GitHubConfigSource(repoOwner: String,
                       repoSlug: String,
                       configFilePath: String,
                       repoBranch: String = "master"): ConfigSource {
        return URLConfigSource(URL("$GITHUB_RAW_BASE_URL/$repoOwner/$repoSlug/$repoBranch/$configFilePath"))
}


private const val BITBUCKET_API_BASE_URL = "https://api.bitbucket.org"
fun BitbucketConfigSource(repoOwner: String,
                          repoSlug: String,
                          configFilePath: String,
                          repoBranch: String = "master",
                          authHeader: String? = null): ConfigSource {
        return URLConfigSource(
                URL("$BITBUCKET_API_BASE_URL/2.0/repositories/$repoOwner/$repoSlug/src/$repoBranch/$configFilePath"),
                authHeader
        )
}

fun BitbucketUserConfigSource(username: String,
                          password: String,
                          repoOwner: String,
                          repoSlug: String,
                          configFilePath: String,
                          repoBranch: String = "master"): ConfigSource {
    return BitbucketConfigSource(repoOwner, repoSlug, configFilePath, repoBranch, basicAuth(username, password))
}

/**
 * Generates `Authorization` header value for basic authentication with the given [username] and [password].
 */
fun basicAuth(username: String, password: String): String {
    val credentials = "$username:$password"
    val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
    return "Basic $encodedCredentials"
}

/**
 * Generates `Authorization` header value for a token (Used in github for example).
 */
fun String.asToken() = "Token $this"
