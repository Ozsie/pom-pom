package com.github.ozsie.pompom.plugins

import com.github.ozsie.pompom.*
import org.redundent.kotlin.xml.Node

fun Node.buildPluginForMvnCompiler(plugin: Map<String, String>) {
    val adapter = getPluginAdapter()
    "/maven-compiler-plugin.json".asResource {
        var modified = it.replace("\$javaVersion", plugin["javaVersion"].toString())
        if (plugin["version"] != null) {
            modified = modified.replace("\$version", plugin["version"].toString())
        } else {
            val version = properties.getProperty("maven-compile-plugin.version")
            modified = modified.replace("\$version", version)
        }


        val mavenCompilerPlugin = adapter.fromJson(modified)

        mavenCompilerPlugin?: throw NullPomException("Plugin should not be null")
        addNode(buildPlugin("org.apache.maven.plugins:maven-compiler-plugin", mavenCompilerPlugin))
    }
}
