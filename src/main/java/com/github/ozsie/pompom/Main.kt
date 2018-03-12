package com.github.ozsie.pompom

import java.io.File
import java.util.concurrent.TimeUnit

private const val DEFAULT_POM = "./pompom.json"
private const val TEMP_OUTPUT = "./temp.pom.xml"

private const val TIMEOUT: Long = 60

fun main(args : Array<String>) {
    when(args[0]) {
        "build" -> println("build")
        "generate" -> run {
            if (args.size == 2) {
                println(generate(args[1]))
            } else {
                println(generate(DEFAULT_POM))
            }
        }
        else -> run {
            println("Generating POM")
            File(TEMP_OUTPUT).printWriter().use { out ->
                out.print(generate(DEFAULT_POM))
            }
            val mvnProcess = ProcessBuilder("mvn", "-f", TEMP_OUTPUT, *args)
                    .directory(File("."))
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor(TIMEOUT, TimeUnit.MINUTES)
        }
    }
}
