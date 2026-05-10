plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    kotlin("plugin.serialization") version "2.0.21"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    implementation("io.ktor:ktor-server-auth:3.0.3")

    implementation("io.ktor:ktor-server-auth-jwt:3.0.3")

    implementation("io.ktor:ktor-server-content-negotiation:3.0.3")

    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")

    implementation("io.ktor:ktor-server-status-pages:3.0.3")

    implementation("io.ktor:ktor-server-cors:3.0.3")

    implementation("org.jetbrains.exposed:exposed-core:0.50.1")

    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")

    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")

    implementation("org.jetbrains.exposed:exposed-java-time:0.50.1")

    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
