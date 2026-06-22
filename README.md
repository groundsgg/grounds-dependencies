# grounds-dependencies

External JVM dependency platform for Grounds projects.

This repository publishes:

```text
gg.grounds:grounds-dependencies:0.1.0
```

The platform pins latest stable external JVM dependency versions used by Grounds JVM projects.

## Included

- Kotlin and kotlinx libraries
- gRPC and Protobuf
- Ktor
- Jackson
- Moshi and OkHttp
- SLF4J
- JUnit, Mockito, and MockK
- NATS, Kubernetes client, Jedis, Nimbus JOSE JWT, and Gson
- Minecraft platform baselines: Minestom, Paper API, and Velocity API

## Not Included

- Grounds-owned artifacts such as `plugin-agones`, `plugin-config`, `plugin-grounds-runtime`, or `grounds-minestom-runtime`
- Quarkus dependencies or `io.quarkus.platform:quarkus-bom`
- Pulumi/npm dependencies
- Gradle plugin versions

## Version Policy

Only latest stable releases are pinned.

Versions containing `alpha`, `beta`, `rc`, `cr`, `milestone`, or `snapshot` are excluded by default. Matching is case-insensitive.

## Gradle Usage

```kotlin
dependencies {
    implementation(platform("gg.grounds:grounds-dependencies:0.1.0"))

    implementation("io.grpc:grpc-api")
    implementation("com.google.protobuf:protobuf-java")
}
```

## Publishing

Releases are managed by Release Please. Version tags publish to GitHub Packages.
