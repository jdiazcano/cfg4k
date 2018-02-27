
import com.jdiazcano.cfg4k.core.toConfig
import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.git.GitConfigSource
import com.winterbe.expekt.should
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class GitConfigSourceTest : Spek({

    val isRunninInTravis = System.getenv()["CI_NAME"] ?: "" == "travis-ci"
    val gitUser = System.getenv("CFGK_GIT_USER")
    val gitPassword = System.getenv("CFGK_GIT_PASS")

    describe("a git config loader 1") {

        it("should load the integer property") {
            val repoDirectory = File("publictest")
            val source = GitConfigSource(
                    "https://github.com/jdiazcano/cfg4k-git-test.git",
                    repoDirectory,
                    "test.properties",
                    loaderGenerator = ::PropertyConfigLoader
            )
            testSource(source)
            repoDirectory.deleteRecursively()
        }

    }


    describe("a git config loader 2") {

        it("should load the integer property") {
            if (isRunninInTravis) {
                val repoDirectory = File("userpasstest")
                val source = GitConfigSource(
                        "https://bitbucket.org/javierdiaz/cfg4k-git-test.git",
                        repoDirectory,
                        "test.properties",
                        loaderGenerator = ::PropertyConfigLoader,
                        credentials = UsernamePasswordCredentialsProvider(gitUser, gitPassword)
                )
                testSource(source)
                repoDirectory.deleteRecursively()
            }
        }
    }

//    describe("a git config loader 3") {
//
//        it("should load the integer property") {
//            if (isRunninInTravis) {
//                val repoDirectory = File("sshtest")
//                val source = GitConfigSource(
//                        "git@bitbucket.org:javierdiaz/cfg4k-git-test.git",
//                        repoDirectory,
//                        "test.properties",
//                        loaderGenerator = ::PropertyConfigLoader,
//                        ssh = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/id_rsa")
//                )
//                testSource(source)
//                repoDirectory.deleteRecursively()
//            }
//        }
//
//    }
//    describe("a git config loader 4") {
//
//        it("should load the integer property") {
//            if (isRunninInTravis) {
//                val repoDirectory = File("sshknownhosttest")
//                val source = GitConfigSource(
//                        "git@bitbucket.org:javierdiaz/cfg4k-git-test.git",
//                        repoDirectory,
//                        "test.properties",
//                        loaderGenerator = ::PropertyConfigLoader,
//                        ssh = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/id_rsa", System.getProperty("user.home") + "/.ssh/known_hosts")
//                )
//                testSource(source)
//                repoDirectory.deleteRecursively()
//            }
//        }
//    }
})

private fun testSource(loader: GitConfigSource) {
    // There is no point on testing more properties because if one is loaded, the rest will be loaded too
    // everything is tested in the providers anyways so these loaders are all ok.
    loader.get("integerProperty").should.be.equal("1".toConfig())
    loader.close()
}