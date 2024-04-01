import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
}

group = "dev.jkcarino"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven {
        // A repository must be speficied for some reason. "registry" is a dummy.
        url = uri("https://maven.pkg.github.com/revanced/registry")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.provider)
    implementation(libs.revanced.patcher)
    implementation(libs.smali)
}

kotlin {
    jvmToolchain(11)
}

tasks {
    withType(Jar::class) {
        manifest {
            attributes["Name"] = "Privacy ReVanced Patches"
            attributes["Description"] = "Patches for ReVanced."
            attributes["Version"] = version
            attributes["Timestamp"] = System.currentTimeMillis().toString()
            attributes["Source"] = "git@github.com:jkennethcarino/privacy-revanced-patches.git"
            attributes["Author"] = "Ken"
            attributes["Contact"] = "6307355+jkennethcarino@users.noreply.github.com"
            attributes["Origin"] = "https://github.com/jkennethcarino"
            attributes["License"] = "GNU General Public License v3.0"
        }
    }

    register("buildDexJar") {
        description = "Build and add a DEX to the JAR file"
        group = "build"

        dependsOn(build)

        doLast {
            val d8 = File(System.getenv("ANDROID_HOME")).resolve("build-tools")
                .listFilesOrdered().last().resolve("d8").absolutePath

            val patchesJar = configurations.archives.get().allArtifacts.files.files.first().absolutePath
            val workingDirectory = layout.buildDirectory.dir("libs").get().asFile

            exec {
                workingDir = workingDirectory
                commandLine = listOf(d8, "--release", patchesJar)
            }

            exec {
                workingDir = workingDirectory
                commandLine = listOf("zip", "-u", patchesJar, "classes.dex")
            }
        }
    }

    // Needed by gradle-semantic-release-plugin.
    // Tracking: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    publish {
        dependsOn("buildDexJar")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jkennethcarino/privacy-revanced-patches")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("revanced-patches-publication") {
            from(components["java"])

            pom {
                name = "Privacy ReVanced Patches"
                description = "Patches for ReVanced."
                url = "https://github.com/jkennethcarino"

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        id = "jkennethcarino"
                        name = "Ken"
                        email = "6307355+jkennethcarino@users.noreply.github.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/jkennethcarino/privacy-revanced-patches.git"
                    developerConnection = "scm:git:git@github.com:jkennethcarino/privacy-revanced-patches.git"
                    url = "https://github.com/jkennethcarino/privacy-revanced-patches"
                }
            }
        }
    }
}

signing {
    useGpgCmd()

    sign(publishing.publications["revanced-patches-publication"])
}
