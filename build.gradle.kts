plugins {
    id("java")
}

group = "com.mathemetric"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(files("../mathops_commons/out/libs/mathops_commons.jar"))
}

tasks.test {
    useJUnitPlatform()
}