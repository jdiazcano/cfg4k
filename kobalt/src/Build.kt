
import com.beust.kobalt.api.Dependencies
import com.beust.kobalt.api.Project
import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.publish.bintray
import com.beust.kobalt.project
import com.beust.kobalt.test

object Versions {
    val cfg4k = "0.8.5"

    object jetbrains {
        val kotlin = "1.2.30"
        val spek = "1.1.2"
        val engine = "1.1.5"
    }
    val bytebuddy = "1.7.9"
    val klaxon = "0.32"
    val typesafe = "1.3.2"
    val jgit = "4.9.0.201710071750-r"
    val expekt = "0.5.0"
    val junitrunner = "1.1.0-M1"
    val snakeyaml = "1.19"
    val jcommander = "1.72"
    val mockwebserver = "3.9.1"
}

object Libraries {
    object eclipse {
        val jgit = "org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit}"
    }
    object jetbrains {
        object kotlin {
            val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.jetbrains.kotlin}"
            val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.jetbrains.kotlin}"
            val test = "org.jetbrains.kotlin:kotlin-test:${Versions.jetbrains.kotlin}"
        }

        object spek {
            val api = "org.jetbrains.spek:spek-api:${Versions.jetbrains.spek}"
            val engine = "org.jetbrains.spek:spek-junit-platform-engine:${Versions.jetbrains.engine}"
        }
    }
    val junitrunner = "org.junit.platform:junit-platform-runner:${Versions.junitrunner}"
    val expekt = "com.winterbe:expekt:${Versions.expekt}"
    val bytebuddy = "net.bytebuddy:byte-buddy:${Versions.bytebuddy}"
    val typesafe = "com.typesafe:config:${Versions.typesafe}"
    val snakeyaml = "org.yaml:snakeyaml:${Versions.snakeyaml}"
    val klaxon = "com.beust:klaxon:${Versions.klaxon}"
    val jcommander = "com.beust:jcommander:${Versions.jcommander}"
    val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.mockwebserver}"
}

fun cfg4kProject(projectName: String, vararg projects: Project, init: Project.() -> Unit) = project(*projects) {
    name = "cfg4k-$projectName"
    group = "com.jdiazcano.jdiazcano"
    artifactId = name
    version = Versions.cfg4k
    directory = "./$name"

    assemble {
        mavenJars {}
    }

    bintray {
        publish = true
    }

    test {
        include("Test*.*", "**/*Test.*", "**/*Tests.*")
    }

    sourceDirectories {
        path("src/main/kotlin", "src/main/resources")
    }

    sourceDirectoriesTest {
        path("src/test/kotlin", "src/test/resources")
    }

    dependenciesTest {
        baseDependencies()
    }

    init()
}

private fun Dependencies.baseDependencies() {
    compile(Libraries.jetbrains.spek.api)
    compile(Libraries.junitrunner)
    compile(Libraries.expekt)
    runtime(Libraries.jetbrains.spek.engine)
}

val core = cfg4kProject("core") {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.jetbrains.kotlin.test)
        compile(Libraries.jetbrains.kotlin.reflect)
    }

    dependenciesTest {
        baseDependencies()
        compile(Libraries.mockwebserver)
    }
}

val bytebuddy = cfg4kProject("bytebuddy", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.bytebuddy)
    }
}

val cli = cfg4kProject("cli", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.jcommander)
    }
}

val git = cfg4kProject("git", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.eclipse.jgit)
    }
}

val hocon = cfg4kProject("hocon", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.typesafe)
    }
}

val json = cfg4kProject("json", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.klaxon)
    }
}

val yaml = cfg4kProject("yaml", core) {
    dependencies {
        compile(Libraries.jetbrains.kotlin.stdlib)
        compile(Libraries.snakeyaml)
    }
}

