package com.jdiazcano.cfg4k.loaders.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.util.FS

open class CustomConfigSessionFactory(
        val keyPath: String,
        val knownHostsFile: String? = null
) : JschConfigSessionFactory() {

    override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
        if (knownHostsFile == null) {
            session?.setConfig("StrictHostKeyChecking", "no")
        }
    }

    @Throws(JSchException::class)
    override fun getJSch(hc: OpenSshConfig.Host, fs: FS): JSch {
        val jsch = super.getJSch(hc, fs)
        jsch.removeAllIdentity()
        jsch.addIdentity(keyPath)
        knownHostsFile?.let { jsch.setKnownHosts(it) }
        return jsch
    }
}
