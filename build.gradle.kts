plugins {
    kotlin("jvm") version "2.2.0-RC"
}

group = "net.leloubil"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
    compilerOptions{
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
