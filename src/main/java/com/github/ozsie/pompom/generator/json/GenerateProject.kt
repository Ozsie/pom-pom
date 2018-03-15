package com.github.ozsie.pompom.generator.json

import com.github.ozsie.pompom.model.DistributionManagement
import com.github.ozsie.pompom.model.Repository
import com.github.ozsie.pompom.model.RepositorySettings
import com.github.ozsie.pompom.model.SCM
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
    var scopeMap = dependencies[scope]
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
    children.forEach { element ->
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

fun Node.getRepositories(type: String): Map<String, Repository>? {
    val repositories = HashMap<String, Repository>()
    children.forEach { element ->
        if (element is Node) {
            when (element.nodeName) {
                type -> {
                    repositories.putAll(buildRepository(element))
                }
            }
        }
    }
    return repositories
}

private fun buildRepository(element: Node): HashMap<String, Repository> {
    val map = HashMap<String, Repository>()
    var id = ""
    var name = ""
    var url = ""
    var layout: String? = null
    var releases: RepositorySettings? = null
    var snapshots: RepositorySettings? = null
    element.children.forEach { child ->
        if (child is Node) {
            when (child.nodeName) {
                "id" -> id = child.getText()
                "name" -> name = child.getText()
                "url" -> url = child.getText()
                "layout" -> layout = child.getText()
                "releases" -> releases = buildRepositorySettings(child)
                "snapshots" -> snapshots = buildRepositorySettings(child)
            }
        }
    }
    map.put(id, Repository(null, name, url, layout, releases, snapshots))
    return map
}

private fun buildRepositorySettings(child: Node): RepositorySettings {
    var enabled: Boolean? = null
    var updatePolicy: String? = null
    child.children.forEach {
        if (it is Node) {
            when (it.nodeName) {
                "enabled" -> enabled = it.getText().toBoolean()
                "updatePolicy" -> updatePolicy = it.getText()
            }
        }
    }
    return RepositorySettings(enabled, updatePolicy)
}

fun Node.getSCM(): SCM {
    var url: String? = null
    var connection: String? = null
    var developerConnection: String? = null
    var tag: String? = null
    children.forEach {
        if (it is Node) {
            when (it.nodeName) {
                "url" -> url = it.getText()
                "connection" -> connection = it.getText()
                "developerConnection" -> developerConnection = it.getText()
                "tag" -> tag = it.getText()
            }
        }
    }

    return SCM(url, connection, developerConnection, tag)
}

fun Node.getDistributionManagement(): DistributionManagement {
    var repository: Repository? = null
    children.forEach {
        if (it is Node && it.nodeName == "repository") {
            val map = buildRepository(it)
            map.forEach { id, r ->
                repository = Repository(id, r.name, r.url, r.layout, r.releases, r.snapshots)
            }
        }
    }
    return DistributionManagement(repository)
}