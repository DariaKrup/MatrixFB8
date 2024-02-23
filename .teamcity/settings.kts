import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.matrix
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

version = "2023.11"

project {
    vcsRoot(customRepo)
    buildType(BuildSecondary)
}

object BuildSecondary : BuildType({
    name = "build_new"

    vcs {
        root(DslContext.settingsRoot, "+:. => subdir")
        root(customRepo,"+:. => subdir")
    }

    steps {

    }

    triggers {
        vcs {  }
    }

    features {

        matrix {
            param("matrix_OS", listOf(
                value("OS_Linux"),
                value("OS_Windows")
            ))
        }
    }
})


object customRepo : GitVcsRoot({
    name = "customRepo"
    url = "https://github.com/DariaKrup/java-maven-junit"
    branch = "refs/heads/master"
})
