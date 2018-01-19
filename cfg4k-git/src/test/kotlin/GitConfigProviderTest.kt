import com.jdiazcano.cfg4k.loaders.PropertyConfigLoader
import com.jdiazcano.cfg4k.loaders.git.CustomConfigSessionFactory
import com.jdiazcano.cfg4k.loaders.git.GitConfigSource
import com.jdiazcano.cfg4k.providers.ConfigProvider
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.get
import com.jdiazcano.cfg4k.reloadstrategies.TimedReloadStrategy
import com.winterbe.expekt.should
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.SshTransport
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.concurrent.TimeUnit

class GitConfigProviderTest : Spek({

    val isRunninInTravis = System.getenv()["CI_NAME"] ?: "" == "travis-ci"
    val reloadFolder = File("gitreloadtest")

    describe("a git config loader 4") {

        it("should load the integer property") {
            if (isRunninInTravis) {
                val loader = GitConfigSource(
                        "git@bitbucket.org:javierdiaz/cfg4k-git-test.git",
                        reloadFolder,
                        "test.properties",
                        loaderGenerator = ::PropertyConfigLoader,
                        ssh = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/rsa_cfg4k", System.getProperty("user.home") + "/.ssh/known_hosts")
                )
                val provider = DefaultConfigProvider(loader, TimedReloadStrategy(1, TimeUnit.SECONDS))
                testProvider(provider)
            }
        }

    }

    afterGroup {
        reloadFolder.deleteRecursively()
    }
})

private fun testProvider(provider: ConfigProvider) {
    // There is no point on testing more properties because if one is loaded, the rest will be loaded too
    // everything is tested in the providers anyways so these loaders are all ok.
    provider.get<Int>("integerProperty").should.be.equal(1)

    val reloadFolder2 = File("gitreloadtest2")
    reloadFolder2.mkdirs()
    val builder = Git.cloneRepository()
            .setURI("git@bitbucket.org:javierdiaz/cfg4k-git-test.git")
            .setBranch("master")
            .setDirectory(reloadFolder2)
            .setTransportConfigCallback { transport ->
                when (transport) {
                    is SshTransport -> transport.sshSessionFactory = CustomConfigSessionFactory(System.getProperty("user.home") + "/.ssh/id_rsa", System.getProperty("user.home") + "/.ssh/known_hosts")
                }
            }

    val clonedRepo = builder.call()
    reloadFolder2.resolve("test.properties").writeText(updatedProperties)
    clonedRepo.add().addFilepattern("test.properties").call()
    clonedRepo.commit().setMessage("Updated properties with millis $millis").call()
    clonedRepo.push().call()
    Thread.sleep(5000)
    provider.get<Long>("longProperty").should.be.equal(millis)

    reloadFolder2.deleteRecursively()

}
private var millis = System.currentTimeMillis()
private val updatedProperties = """a=b
c=d
integerProperty=1
longProperty=$millis
shortProperty=1
doubleProperty=1.1
floatProperty=2.1
byteProperty=2
list=1,2,3
floatList=1.2,2.2,3.2
booleanProperty=true
enumList=A,B
bigIntegerProperty=1
bigDecimalProperty=1.1
toString=this should not be ever used
dateProperty=01-01-2017
calendarProperty=01-01-2017
localDateProperty=01-01-2017
isoLocalDateProperty=2017-01-01
localDateTimeProperty=01-01-2017 18:01:31
isoLocalDateTimeProperty=2017-01-01T18:01:31
zonedDateTimeProperty=01-01-2017 18:01:31
isoZonedDateTimeProperty=2017-01-01T18:01:31+01:00
offsetDateTimeProperty=01-01-2017 18:01:31+01:00
isoOffsetDateTimeProperty=2017-01-01T18:01:31+01:00
offsetTimeProperty=18:01:31+01:00
isoOffsetTimeProperty=18:01:31+01:00"""