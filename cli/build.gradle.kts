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

        val main by compilations.getting
        val fileIo by main.cinterops.creating {
            defFile("./src/nativeMain/c/fileIo.def")
            packageName("com.github.landgrafhomyak.itmo_bevm.cli")
        }

        binaries {
            executable {

                entryPoint = "com.github.landgrafhomyak.itmo_bevm.cli.mainNative"
                this.outputDirectory = projectDir.resolve("out")
                baseName = "bevm"
                buildType
                if (buildType == org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE)
                    tasks.create<Copy>("collectCli") {
//                        println("This task used only for GitHub CI")
                        dependsOn(this@executable.linkTask)
                        from(this@executable.outputFile)
                        into(rootProject.projectDir.resolve("dist"))
                    }
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":"))
            }
        }
        val nativeMain by getting
        val jvmMain by getting
    }
}
