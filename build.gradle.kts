import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kt.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.config4k)
    implementation(libs.mqtt.paho)
    implementation(libs.slf4j.simple)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.jackson)
    implementation(libs.ktor.client.ws)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    applicationName = "apx"
    mainClass.set("io.heapy.smarthome.MainKt")
}
