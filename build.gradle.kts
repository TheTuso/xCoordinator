plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "pl.tuso.coordinator"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    implementation("io.lettuce:lettuce-core:6.1.8.RELEASE")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.0")
    compileOnly("net.luckperms:api:5.4")
}

tasks {
    shadowJar {
        relocate("io.lettuce.core", "pl.tuso.lib")

        archiveBaseName.set("xCoordinator")
        archiveClassifier.set("")
        archiveVersion.set(version)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}