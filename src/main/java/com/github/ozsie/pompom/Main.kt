package com.github.ozsie.pompom

import java.io.File
import java.util.Properties
import java.util.concurrent.TimeUnit

private const val DEFAULT_POM_POM = "./pompom.json"
private const val OUTPUT_POM_XML = "./pom.xml"

private const val TIMEOUT: Long = 60

val properties: Properties = "/default-values.properties".asProperties()

fun String.asProperties(): Properties {
    val content = this.javaClass::class.java.getResource(this).openStream()
    val properties = Properties()
    properties.load(content)
    return properties
}

fun main(args : Array<String>) {

    when(args[0]) {
        "build" -> println("build")
        "generate" -> run {
            if (args.size == 2) {
                println(generate(args[1]))
            } else {
                println(generate(DEFAULT_POM_POM))
            }
        }
        else -> run {
            println("Generating POM")
            File(OUTPUT_POM_XML).printWriter().use { out ->
                out.print(generate(DEFAULT_POM_POM))
            }
            val mvnProcess = ProcessBuilder("mvn", "-f", OUTPUT_POM_XML, *args)
                    .directory(File("."))
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor(TIMEOUT, TimeUnit.MINUTES)
        }
    }
}
