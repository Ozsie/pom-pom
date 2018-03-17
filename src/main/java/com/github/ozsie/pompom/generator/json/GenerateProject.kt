package com.github.ozsie.pompom.generator.json

import com.github.ozsie.pompom.generator.getText
import com.github.ozsie.pompom.model.*
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.TextElement

fun Node.getProperties(): Map<String, String>? {
    val properties = HashMap<String, String>()
    children.filter { it is Node }.forEach { it as Node
        properties.put(it.nodeName, it.getText())
    }

    return properties
}

fun Node.getSCM(): SCM {
    var url: String? = null
    var connection: String? = null
    var developerConnection: String? = null
    var tag: String? = null
    children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "url" -> url = it.getText()
            "connection" -> connection = it.getText()
            "developerConnection" -> developerConnection = it.getText()
            "tag" -> tag = it.getText()
        }
    }

    return SCM(url, connection, developerConnection, tag)
}

fun Node.getDistributionManagement(): DistributionManagement {
    var repository: Repository? = null
    children.filter { it is Node && it.nodeName == "repository" }.forEach { it as Node
        val map = buildRepository(it)
        map.forEach { id, (_, name, url, layout, releases, snapshots) ->
            repository = Repository(id, name, url, layout, releases, snapshots)
        }
    }
    return DistributionManagement(repository)
}
