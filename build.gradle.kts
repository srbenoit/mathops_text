plugins {
    id("java")
}

group = "dev.mathops.text"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(files("../mathops_commons/out/libs/mathops_commons.jar"))
}

tasks.test {
    useJUnitPlatform()
}