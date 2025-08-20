plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.9.10"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // --- Ktor 3.x (unifica versiones) ---
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation("io.ktor:ktor-server-call-logging:3.0.0")
    implementation("io.ktor:ktor-server-status-pages:3.0.0")
    implementation("io.ktor:ktor-server-config-yaml:3.0.0")

    // --- Logging ---
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // --- AWS SDK v2 ---
    implementation(platform("software.amazon.awssdk:bom:2.25.43"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:url-connection-client")
    // --- Tests ---
    testImplementation("io.ktor:ktor-server-test-host:3.0.0")
    testImplementation(kotlin("test"))
}

