package com.jdiazcano.cfg4k.loaders.git

import com.jdiazcano.cfg4k.core.ConfigObject
import com.jdiazcano.cfg4k.loaders.ConfigLoader
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.sources.ConfigSource
import com.jdiazcano.cfg4k.sources.FileConfigSource
import com.jdiazcano.cfg4k.utils.SettingsNotInitialisedException
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.SshTransport
import java.io.Closeable
import java.io.File

/**
 * Git config loader. This loader will connect to a git repository (public or private), clone it and use these files
 * to configure the loader inside it. This will only act as a bridge for cloning the repo and you must provide the
 * config loader generator (which is only a reference to a normal loader constructor).
 *
 * This resource can (and should) be closed but if it is not closed and you use the same directory it will open the git
 * repository instead of trying to clone it again
 */
class GitConfigSource(
        uri: String,
        private val repoDirectory: File,
        private val configFilePath: String,
        branch: String = "master",
        private val credentials: CredentialsProvider? = null,
        private val ssh: CustomConfigSessionFactory? = null,
        private val loaderGenerator: (ConfigSource) -> ConfigLoader = ::PropertyConfigLoader
) : ConfigLoader, Closeable {

    private val clonedRepo: Git
    private var initialised = false
    private lateinit var loader: ConfigLoader

    init {
        // If the repo exist we just open the directory
        if (repoDirectory.exists()) {
            clonedRepo = Git.open(repoDirectory)
        } else {
            repoDirectory.mkdirs()
            val builder = Git.cloneRepository()
                    .setURI(uri)
                    .setBranch(branch)
                    .setDirectory(repoDirectory)

            if (credentials != null) {
                builder.setCredentialsProvider(credentials)
            } else if (ssh != null) {
                builder.setTransportConfigCallback { transport ->
                    when (transport) {
                        is SshTransport -> transport.sshSessionFactory = ssh
                    }
                }
            }

            clonedRepo = builder.call()
        }
        initProperties()
    }

    private fun initProperties() {
        // This is needed to set the instance of the SshSessionFactory if there is no known hosts file because otherwise
        // the StrictHostKeyChecking property must be set to false in order to continue with the clone
        if (ssh != null && ssh.knownHostsFile == null) {
            SshSessionFactory.setInstance(ssh)
        }

        clonedRepo.pull().setCredentialsProvider(credentials).call()
        loader = loaderGenerator(FileConfigSource(repoDirectory.resolve(configFilePath)))

        initialised = true
    }

    override fun get(key: String): ConfigObject? {
        if (!initialised) {
            throw SettingsNotInitialisedException("Settings have not been initialised. Git repository is being cloned")
        }

        return loader.get(key)
    }

    override fun reload() {
        initProperties()
    }

    override fun close() {
        clonedRepo.close()
        repoDirectory.deleteRecursively()
    }
}