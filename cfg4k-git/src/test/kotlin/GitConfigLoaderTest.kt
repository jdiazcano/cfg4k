import com.jdiazcano.konfig.loaders.JsonConfigLoader
import com.jdiazcano.konfig.loaders.PropertyConfigLoader
import com.jdiazcano.konfig.loaders.git.CustomConfigSessionFactory
import com.jdiazcano.konfig.loaders.git.GitConfigLoader
import com.winterbe.expekt.should
import org.eclipse.jgit.transport.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File

class GitConfigLoaderTest: Spek({
    describe("a git config loader 1") {

        it("should load the integer property") {
            val loader = GitConfigLoader(
                    "https://github.com/jdiazcano/cfg4k-git-test.git",
                    File("publictest"),
                    "test.json",
                    loaderGenerator = ::JsonConfigLoader
            )
            testLoader(loader)
        }

    }

    describe("a git config loader 2") {

        it("should load the integer property") {
            val loader = GitConfigLoader(
                    "https://bitbucket.org/javierdiaz/cfg4k-git-test.git",
                    File("userpasstest"),
                    "test.properties",
                    loaderGenerator = ::PropertyConfigLoader,
                    credentials = UsernamePasswordCredentialsProvider(System.getenv("CFGK_GIT_USER"), System.getenv("CFGK_GIT_PASS"))
            )
            testLoader(loader)
        }
    }

    describe("a git config loader 3") {

        it("should load the integer property") {
            val loader = GitConfigLoader(
                    "git@bitbucket.org:javierdiaz/cfg4k-git-test.git",
                    File("sshtest"),
                    "test.json",
                    loaderGenerator = ::JsonConfigLoader,
                    ssh = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/id_rsa")
            )
            testLoader(loader)
        }

    }
    describe("a git config loader 4") {

        it("should load the integer property") {
            val loader = GitConfigLoader(
                    "git@bitbucket.org:javierdiaz/cfg4k-git-test.git",
                    File("sshknownhosttest"),
                    "test.json",
                    loaderGenerator = ::JsonConfigLoader,
                    ssh = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/id_rsa", System.getProperty("user.home") + "/.ssh/known_hosts")
            )
            testLoader(loader)
        }

    }
})

private fun testLoader(loader: GitConfigLoader) {
    // There is no point on testing more properties because if one is loaded, the rest will be loaded too
    // everything is tested in the providers anyways so these loaders are all ok.
    loader.get("integerProperty").should.be.equal("1")
    loader.close()
}