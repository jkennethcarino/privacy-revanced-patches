group = "dev.jkcarino"

plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.provider)
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks {
    shadowJar {
        archiveClassifier = ""

        manifest {
            exclude("META-INF/versions/**")
        }
        dependencies {
            include(dependency("org.bouncycastle:.*"))
            relocate("org.bouncycastle", "shadow.org.bouncycastle")
        }
        minimize()
    }

    named("buildAndroid").configure {
        dependsOn(shadowJar)
    }
}

patches {
    about {
        name = "Privacy ReVanced Patches"
        description = "Privacy Patches for ReVanced"
        source = "git@github.com:jkennethcarino/privacy-revanced-patches.git"
        author = "Ken"
        contact = "6307355+jkennethcarino@users.noreply.github.com"
        website = "https://github.com/jkennethcarino"
        license = "GNU General Public License v3.0"
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
}
