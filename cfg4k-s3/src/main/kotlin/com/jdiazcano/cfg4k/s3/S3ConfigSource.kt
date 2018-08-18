package com.jdiazcano.cfg4k.s3

import com.amazonaws.services.s3.AmazonS3
import com.jdiazcano.cfg4k.sources.ConfigSource
import java.io.InputStream

/**
 * Given an S3 Client, bucket and objectName it will convert it to a ConfigSource.
 *
 * @since 0.8.6
 */
class S3ConfigSource(
        private val client: AmazonS3,
        private val bucket: String,
        private val objectName: String
) : ConfigSource {

    override fun read(): InputStream {
        return client.getObject(bucket, objectName).objectContent
    }

    override fun toString(): String {
        return "S3ConfigSource(client=$client, bucket='$bucket', objectName='$objectName')"
    }

}