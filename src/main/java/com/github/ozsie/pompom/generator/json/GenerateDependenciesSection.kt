package com.github.ozsie.pompom.generator.json

import org.redundent.kotlin.xml.Node

fun Node.getDependencies(): Map<String, Any>? {
    val dependencies = HashMap<String, Any>()
    children.filter { it is Node }.forEach { it as Node
        val dependency = it.getProperties()
        if (dependency != null) {
            val artifact = "${dependency["groupId"]}:${dependency["artifactId"]}"
            val version = dependency["version"]
            val scope = dependency["scope"]
            if (scope == "compile") {
                dependencies.put(artifact, version as Any)
            } else {
                noneDefaultScope(dependencies, scope, artifact, version)
            }
        }
    }

    return dependencies
}

fun noneDefaultScope(dependencies: HashMap<String, Any>, scope: String?, artifact: String, version: String?) {
    var scopeMap = dependencies[scope]
    if (scopeMap == null) {
        scopeMap = HashMap<String, Any>()
        scopeMap.put(artifact, version as Any)
        dependencies.put(scope as String, scopeMap)
    } else {
        (scopeMap as HashMap<String, Any>).put(artifact, version as Any)
    }
}
