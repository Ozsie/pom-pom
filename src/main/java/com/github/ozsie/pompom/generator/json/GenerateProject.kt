package com.github.ozsie.pompom.generator.json

import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.TextElement

fun Node.getDependencies(): Map<String, Any>? {
    val dependencies = HashMap<String, Any>()
    for (element in children) {
        if (element is Node) {
            val dependency = element.getProperties()
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
    }

    return dependencies
}

fun noneDefaultScope(dependencies: HashMap<String, Any>, scope: String?, artifact: String, version: String?) {
    var scopeMap = dependencies.get(scope)
    if (scopeMap == null) {
        scopeMap = HashMap<String, Any>()
        scopeMap.put(artifact, version as Any)
        dependencies.put(scope as String, scopeMap)
    } else {
        (scopeMap as HashMap<String, Any>).put(artifact, version as Any)
    }
}

fun Node.getProperties(): Map<String, String>? {
    val properties = HashMap<String, String>()
    for (element in children) {
        if (element is Node) {
            properties.put(element.nodeName, element.getText())
        }
    }

    return properties
}

fun Node.getText(): String {
    return (this.first {
        it is TextElement
    } as TextElement).text.trim()
}