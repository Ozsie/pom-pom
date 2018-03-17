package com.github.ozsie.pompom.generator.json

import com.github.ozsie.pompom.generator.getText
import com.github.ozsie.pompom.model.Repository
import com.github.ozsie.pompom.model.RepositorySettings
import org.redundent.kotlin.xml.Node

fun Node.getRepositories(type: String): Map<String, Repository>? {
    val repositories = HashMap<String, Repository>()
    children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            type -> {
                repositories.putAll(buildRepository(it))
            }
        }
    }
    return repositories
}

fun buildRepository(element: Node): HashMap<String, Repository> {
    val map = HashMap<String, Repository>()
    var id = ""
    var name = ""
    var url = ""
    var layout: String? = null
    var releases: RepositorySettings? = null
    var snapshots: RepositorySettings? = null
    element.children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "id" -> id = it.getText()
            "name" -> name = it.getText()
            "url" -> url = it.getText()
            "layout" -> layout = it.getText()
            "releases" -> releases = buildRepositorySettings(it)
            "snapshots" -> snapshots = buildRepositorySettings(it)
        }
    }
    map.put(id, Repository(null, name, url, layout, releases, snapshots))
    return map
}

fun buildRepositorySettings(child: Node): RepositorySettings {
    var enabled: Boolean? = null
    var updatePolicy: String? = null
    child.children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "enabled" -> enabled = it.getText().toBoolean()
            "updatePolicy" -> updatePolicy = it.getText()
        }
    }
    return RepositorySettings(enabled, updatePolicy)
}
