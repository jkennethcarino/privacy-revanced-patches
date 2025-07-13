plugins {
    id(libs.plugins.android.library.get().pluginId)
}

android {
    namespace = "dev.jkcarino.extension"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
