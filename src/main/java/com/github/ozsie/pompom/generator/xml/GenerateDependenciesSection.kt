package com.github.ozsie.pompom.generator.xml

import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun buildDependencies(dependencies: Map<String, Any>): Node = with(dependencies) {
    return xml("dependencies") {
        dependencies.forEach {
            when (it.value) {
                is String -> addNode(buildDependency(it.key, it.value.toString()))
                else -> {
                    val scope = it.key
                    if (it.value is Map<*, *>) {
                        val map = (it.value as Map<String, String>)
                        for (key in map.keys) {
                            val version = map[key]
                            if (version != null) {
                                addNode(buildDependency(key, version, scope))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun buildDependency(artifact: String, version: String, scope: String = "compile"): Node {
    return xml("dependency") {
        "groupId" { -artifact.substringBefore(":") }
        "artifactId" { -artifact.substringAfter(":") }
        "version" { -version }
        "scope" { -scope }
    }
}
