plugins {
    java
}

group = "gg.grounds"
version = "0.1.0-SNAPSHOT"

dependencies {
    implementation(platform("gg.grounds:grounds-dependencies:0.1.0-SNAPSHOT"))
    implementation("io.grpc:grpc-api")
    implementation("com.google.protobuf:protobuf-java")
    implementation("org.slf4j:slf4j-api")
    implementation("net.minestom:minestom")
    compileOnly("io.papermc.paper:paper-api")
    compileOnly("com.velocitypowered:velocity-api")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
