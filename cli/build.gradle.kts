plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

configurations {
    create("cli")
}


kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux"    -> linuxX64("native")
        isMingwX64           -> mingwX64("native")
        else                 -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
                this.outputDirectory = projectDir.resolve("out")
                baseName = "bevm"
                println("ertyuiop[")
                tasks.findByName("collectCli") ?: tasks.create<Copy>("collectCli") {
                    println("This task used only for GitHub CI")
                    dependsOn(tasks["build"])
                    from(this@executable.outputFile)
                    into(rootProject.projectDir.resolve("dist"))
                }
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":"))
            }
        }
        val nativeTest by getting
    }
}
