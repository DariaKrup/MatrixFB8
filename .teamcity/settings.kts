import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.matrix
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

version = "2023.11"

project {
vcsRoot(customRepo)
    buildType(Build1)
}

object Build1 : BuildType({
    name = "build1"

    vcs {
        root(DslContext.settingsRoot)
        root(customRepo)
    }

    steps {
        script {
            id = "simpleRunner"
            scriptContent = "ls"
        }
    }
features {
    matrix {
        //enabled = false
        param("par1", listOf(
            value("1"),
            value("2")
        ))
    }
}
})


object customRepo : GitVcsRoot({
    name = "customRepo"
    url = "https://github.com/ChubatovaTiger/mavenJunit"
    branch = "refs/heads/main"
})
