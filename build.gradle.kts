plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

val minecraftVersion: String by extra
val modVersion: String by extra
val neoVersion: String by extra
val parchmentMappingsVersion: String by extra
val parchmentMinecraftVersion: String by extra
val neoforgeVersionRange: String by extra
val jeiVersion: String by extra

val localRuntime: Configuration by configurations.creating

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

version = modVersion
group = "com.smashingmods.chemlib"

repositories {
    maven("https://maven.blamejared.com/")
}

base {
    archivesName = "chemlib"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }
    
    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("data") {
            data()
            programArguments.addAll("--mod", "chemlib", "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.INFO
        }
    }

    mods {
        create("chemlib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

configurations {
    runtimeClasspath.get().extendsFrom(localRuntime)
}

dependencies {
    compileOnly("mezz.jei:jei-$minecraftVersion-common-api:${jeiVersion}")
    compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:${jeiVersion}")
    localRuntime("mezz.jei:jei-$minecraftVersion-neoforge:${jeiVersion}")
}

tasks.processResources {
    var replaceProperties = mapOf("minecraftVersion" to minecraftVersion, "neoVersion" to neoVersion,
        "neoforgeVersionRange" to neoforgeVersionRange, "modVersion" to modVersion
    )

    inputs.properties(replaceProperties)
    
    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

/*
publishing {
    publications {
        register("mavenJava", MavenPublication) {
            from components.java
        }
    }
    repositories {
        maven {
            url "file://${project.projectDir}/repo"
        }
    }
}
*/
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

/*
publishing {
    publications {
        mavenJava(MavenPublication) {
            afterEvaluate {
                artifact project.jar
                artifact project.sourcesJar
                artifact project.javadocJar
            }
            setGroupId "smashingmods"
            setArtifactId "chemlib"
        }
    }
    repositories {
        maven {
            url "https://maven.tamaized.com/releases"
            credentials {
                username secrets.getProperty("maven_username")
                password secrets.getProperty("maven_password")
            }
        }
    }
}
 */