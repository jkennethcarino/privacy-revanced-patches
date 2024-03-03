rootProject.name = "privacy-revanced-patches"

buildCache {
    local {
        isEnabled = "CI" !in System.getenv()
    }
}
