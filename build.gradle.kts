import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "me.cjcrafter"
version = "1.0.1"

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

// See https://github.com/Minecrell/plugin-yml
bukkit {
    main = "me.cjcrafter.simpleworlds.SimpleWorlds"
    apiVersion = "1.13"

    authors = listOf("CJCrafter")
    depend = listOf("MechanicsCore")
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsMain")
        credentials {
            username = findProperty("user").toString()
            password = findProperty("pass").toString() // Check WeaponMechanics wiki on how to use this in your repo!
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("me.deecaad:mechanicscore:1.8.3")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks.named<ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("SimpleWorlds-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate ("org.bstats", "me.cjcrafter.simpleworlds.lib.bstats") {
            include(dependency("org.bstats:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(16)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}