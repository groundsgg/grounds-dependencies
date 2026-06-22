import org.gradle.api.GradleException
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository

plugins {
    `java-platform`
    `maven-publish`
}

group = "gg.grounds"
version = providers.gradleProperty("versionOverride").orElse("0.1.0-SNAPSHOT").get()

val stableVersionPattern =
    Regex(
        pattern = "(alpha|beta|rc|cr|milestone|snapshot|dev|eap|preview|nightly|canary|experimental|[-.]m\\d+)",
        option = RegexOption.IGNORE_CASE,
    )

val managedVersions =
    linkedMapOf(
        "org.jetbrains.kotlin:kotlin-stdlib" to "2.4.0",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8" to "2.4.0",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core" to "1.11.0",
        "org.jetbrains.kotlinx:kotlinx-serialization-json" to "1.11.0",
        "io.grpc:grpc-api" to "1.82.0",
        "io.grpc:grpc-core" to "1.82.0",
        "io.grpc:grpc-context" to "1.82.0",
        "io.grpc:grpc-stub" to "1.82.0",
        "io.grpc:grpc-protobuf" to "1.82.0",
        "io.grpc:grpc-netty-shaded" to "1.82.0",
        "com.google.protobuf:protobuf-java" to "4.35.1",
        "com.google.protobuf:protobuf-kotlin" to "4.35.1",
        "io.ktor:ktor-client-core" to "3.5.0",
        "io.ktor:ktor-client-cio" to "3.5.0",
        "tools.jackson.core:jackson-databind" to "3.2.0",
        "tools.jackson.module:jackson-module-kotlin" to "3.2.0",
        "tools.jackson.dataformat:jackson-dataformat-yaml" to "3.2.0",
        "com.squareup.moshi:moshi" to "1.15.2",
        "com.squareup.moshi:moshi-kotlin-codegen" to "1.15.2",
        "com.squareup.okhttp3:okhttp" to "5.4.0",
        "com.squareup.okhttp3:okhttp-sse" to "5.4.0",
        "com.squareup.okhttp3:mockwebserver" to "5.4.0",
        "com.google.code.gson:gson" to "2.14.0",
        "org.slf4j:slf4j-api" to "2.0.18",
        "org.slf4j:slf4j-simple" to "2.0.18",
        "org.junit.jupiter:junit-jupiter" to "6.1.0",
        "org.junit.jupiter:junit-jupiter-api" to "6.1.0",
        "org.junit.jupiter:junit-jupiter-engine" to "6.1.0",
        "org.junit.platform:junit-platform-launcher" to "6.1.0",
        "org.mockito:mockito-core" to "5.23.0",
        "org.mockito.kotlin:mockito-kotlin" to "6.3.0",
        "io.mockk:mockk" to "1.14.11",
        "io.nats:jnats" to "2.25.3",
        "io.kubernetes:client-java" to "26.0.0",
        "redis.clients:jedis" to "7.5.2",
        "com.nimbusds:nimbus-jose-jwt" to "10.9.1",
        "net.minestom:minestom" to "2026.06.20-26.1.2",
        "io.papermc.paper:paper-api" to "26.1.2.build.72-stable",
        "com.velocitypowered:velocity-api" to "3.4.0",
    )

dependencies {
    constraints {
        managedVersions.forEach { (coordinates, version) ->
            api("$coordinates:$version")
        }
    }
}

val validateStableConstraints =
    tasks.register("validateStableConstraints") {
        group = "verification"
        description = "Validates that managed dependency versions are stable releases."

        doLast {
            val unstable =
                managedVersions.filterValues { version ->
                    stableVersionPattern.containsMatchIn(version)
                }

            if (unstable.isNotEmpty()) {
                val details =
                    unstable.entries.joinToString(separator = System.lineSeparator()) {
                        (coordinates, version) ->
                        "$coordinates:$version"
                    }
                throw GradleException(
                    "Managed dependency versions must be stable releases. Unstable constraints:${System.lineSeparator()}$details"
                )
            }
        }
    }

val validateConsumerFixture =
    tasks.register<Exec>("validateConsumerFixture") {
        group = "verification"
        description = "Validates that the local consumer fixture builds against the published platform."
        dependsOn("publishToMavenLocal")
        workingDir = layout.projectDirectory.dir("fixtures/consumer").asFile
        commandLine(
            layout.projectDirectory.file("gradlew").asFile.absolutePath,
            "build",
            "--no-daemon",
            "-PgroundsDependenciesVersion=$version",
        )
    }

tasks.named("check") {
    dependsOn(validateStableConstraints)
    dependsOn(validateConsumerFixture)
}

tasks.named("publish") {
    dependsOn(validateStableConstraints)
}

tasks.named("publishToMavenLocal") {
    dependsOn(validateStableConstraints)
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(validateStableConstraints)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
            artifactId = "grounds-dependencies"

            pom {
                name.set("Grounds Dependencies")
                description.set("External JVM dependency platform for Grounds projects.")
                url.set("https://github.com/groundsgg/grounds-dependencies")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("groundsgg")
                        name.set("Grounds")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/groundsgg/grounds-dependencies.git")
                    developerConnection.set(
                        "scm:git:ssh://git@github.com/groundsgg/grounds-dependencies.git"
                    )
                    url.set("https://github.com/groundsgg/grounds-dependencies")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/groundsgg/grounds-dependencies")
            credentials {
                username =
                    providers.gradleProperty("github.user")
                        .orElse(providers.environmentVariable("GITHUB_ACTOR"))
                        .orNull
                password =
                    providers.gradleProperty("github.token")
                        .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                        .orNull
            }
        }
    }
}
