plugins {
    kotlin("jvm") version "2.1.20"
    id("me.champeau.jmh") version "0.7.2"
}

group = "com.github.fioan89"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    jmhImplementation("org.openjdk.jmh:jmh-core:1.37")
    jmhImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    jmhImplementation("org.zeroturnaround:zt-exec:1.12")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}