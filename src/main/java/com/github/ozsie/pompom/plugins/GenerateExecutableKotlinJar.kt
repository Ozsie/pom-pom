package com.github.ozsie.pompom.plugins

import com.github.ozsie.pompom.*
import org.redundent.kotlin.xml.Node

fun Node.buildPluginsForExecutableKotlin(plugin: Map<String, String>, finalName: String?) {
    val adapter = getPluginAdapter()
    "/maven-jar-plugin.json".asResource {
        var modified = it.
                replace("\$mainClass", plugin["mainClass"].toString())
        if (plugin["mvnJarVersion"] != null) {
            modified = modified.replace("\$version", plugin["mvnJarVersion"].toString())
        } else {
            val version = properties.getProperty("maven-jar-plugin.version")
            modified = modified.replace("\$version", version)
        }
        if (plugin["addClassPath"] != null) {
            modified = modified.replace("\$addClasspath", plugin["addClasspath"].toString())
        } else {
            val addClasspath = properties.getProperty("maven-jar-plugin.addClasspath")
            modified = modified.replace("\$addClasspath", addClasspath)
        }

        val mavenJarPlugin = adapter.fromJson(modified)

        mavenJarPlugin?: throw NullPomException("Plugin should not be null")
        addNode(buildPlugin("org.apache.maven.plugins:maven-jar-plugin", mavenJarPlugin))
    }

    if (plugin["descriptorRef"] != null) {
        "/maven-assembly-plugin.json".asResource {
            var modified = it.replace("\$mainClass", plugin["mainClass"].toString()).
                    replace("\$descriptorRef", plugin["descriptorRef"].toString())
            if (plugin["mvnAssemblyVersion"] != null) {
                modified = modified.replace("\$version", plugin["mvnAssemblyVersion"].toString())
            } else {
                val version = properties.getProperty("maven-assembly-plugin.version")
                modified = modified.replace("\$version", version)
            }
            if (plugin["finalName"] != null) {
                modified = modified.replace("\$finalName", "$finalName-${plugin["finalName"].toString()}")
            } else {
                modified = modified.replace("\$finalName", "$finalName-${plugin["descriptorRef"].toString()}")
            }
            if (plugin["appendAssemblyId"] != null) {
                modified = modified.replace("\$appendAssemblyId", plugin["appendAssemblyId"].toString())
            } else {
                val appendAssemblyId = properties.getProperty("maven-assembly-plugin.addClasspath")
                modified = modified.replace("\$appendAssemblyId", appendAssemblyId)
            }

            val mavenAssemblyPlugin = adapter.fromJson(modified)

            mavenAssemblyPlugin?: throw NullPomException("Plugin should not be null")
            addNode(buildPlugin("org.apache.maven.plugins:maven-assembly-plugin", mavenAssemblyPlugin))
        }
    }
}
