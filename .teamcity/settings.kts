import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.ideaInspections
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    buildType(BuildSecondary)

    features {
        awsConnection {
            id = "AmazonWebServicesAws"
            name = "Amazon Web Services (AWS)"
            regionName = "eu-west-1"
            credentialsType = static {
                accessKeyId = "AKIA5JH2VERVI62P5XDY"
                secretAccessKey = "credentialsJSON:49f6615f-a81a-4ab4-949d-bbd03b437fe1"
                stsEndpoint = "https://sts.eu-west-1.amazonaws.com"
            }
            allowInBuilds = false
        }
        dockerRegistry {
            id = "PROJECT_EXT_10"
            name = "Docker Registry"
            userName = "dariakrup"
            password = "credentialsJSON:f2716b01-9561-48d1-9ede-1c41e0c257ae"
        }
        amazonEC2CloudProfile {
            id = "amazon-1"
            name = "Cloud AWS Profile"
            serverURL = "http://10.128.93.57:8154"
            terminateIdleMinutes = 30
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:a1efd2f6-61a8-4744-bc00-c0549617099f"
                secretKey = "credentialsJSON:49f6615f-a81a-4ab4-949d-bbd03b437fe1"
            }
        }
    }
}

object BuildSecondary : BuildType({
    name = "build_new"

    params {
        param("env.JDK_11", "/usr/lib/jvm/java-11-openjdk-amd64")
        param("env.JDK_17_0", "/usr/lib/jvm/java-17-openjdk-amd64")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            enabled = false
            goals = "clean test"
            pomLocation = ".teamcity/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        script {
            name = "Echo parameters"
            id = "Echo_parameters"
            scriptContent = "echo %build.counter% %build.number%"
        }
        ideaInspections {
            id = "Inspection"
            enabled = false
            pathToProject = ".teamcity/pom.xml"
            jvmArgs = "-Xmx1024m -XX:ReservedCodeCacheSize=512m"
            targetJdkHome = "%env.JDK_17_0%"
            ideaAppHome = "%teamcity.tool.intellij.DEFAULT%"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
