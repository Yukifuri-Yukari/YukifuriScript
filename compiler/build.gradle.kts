plugins {
    kotlin("jvm")

    id("com.gradleup.shadow") version "9.3.1"
}

group = "yukifuri.script.compiler"
version = "Indev-0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(fileTree("$rootDir/version/lib") {
        include("*.jar")
    })
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

//////////
version = "Indev-0.1.1"

tasks.jar {
    archiveBaseName.set("YukifuriScript-Compiler")

    manifest {
        attributes["Main-Class"] = "yukifuri.script.compiler.MainKt"
        attributes["Class-Path"] = listOf(
            "libraries\\Yukifuri-Utils-0.1.0-alpha-snapshot.jar", // MyLib
            "libraries\\annotations-23.0.0.jar",
            "libraries\\error_prone_annotations-2.27.0.jar",
            "libraries\\kotlin-stdlib-2.2.20.jar",
            "libraries\\kotlinx-coroutines-core-jvm-1.10.2.jar",
            "libraries\\kotlinx-datetime-jvm-0.6.2.jar",
            "libraries\\kotlinx-serialization-core-jvm-1.8.1.jar",
            "libraries\\kotlinx-serialization-json-jvm-1.8.1.jar",
        ).joinToString(" ")
    }
}

fun getJarName(): String {
    return "${tasks.jar.get().archiveBaseName.get()}-${tasks.jar.get().archiveVersion.get()}"
}

project.copy {
    if (File("${rootDir.path}\\version\\generated\\${getJarName()}.jar").exists()) {
        File("${rootDir.path}\\version\\generated\\${getJarName()}.jar").delete()
    }
    from("${project.projectDir}\\build\\libs\\${getJarName()}.jar")
    into("${rootDir.path}\\version\\generated")
}
//////
