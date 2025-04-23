group = "dev.jkcarino"

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
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
