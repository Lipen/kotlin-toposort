import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.Lipen"

plugins {
    kotlin("jvm") version "1.7.21"
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
    id("com.github.ben-manes.versions") version "0.44.0"
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.github.microutils:kotlin-logging:3.0.4")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

jgitver {
    strategy("MAVEN")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven(url = "$buildDir/repository")
    }
}

tasks.wrapper {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

defaultTasks("clean", "build")
