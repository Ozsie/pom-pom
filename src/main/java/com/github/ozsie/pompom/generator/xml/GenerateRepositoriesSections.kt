package com.github.ozsie.pompom.generator.xml

import com.github.ozsie.pompom.model.Repository
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun buildRepositories(type: String, repositories: Map<String, Repository>): Node {
    return xml(type) {
        repositories.forEach {
            id, repository -> run {
            when(type) {
                "repositories" -> addNode(buildRepository("repository", id, repository))
                "pluginRepositories" -> addNode(buildRepository("pluginRepository", id, repository))
            }
        }
        }
    }
}

fun buildRepository(type: String, id: String, repository: Repository): Node {
    return xml(type) {
        "id" { -id }
        "name" { -repository.name }
        "url" { -repository.url }
        if (repository.layout != null) {
            "layout" { -repository.layout }
        }
        if (repository.releases != null) {
            "releases" {
                if (repository.releases.enabled != null) {
                    "enabled" { -"${repository.releases.enabled}" }
                }
                if (repository.releases.updatePolicy != null) {
                    "updatePolicy" { -"${repository.releases.updatePolicy}" }
                }
            }
        }
        if (repository.snapshots != null) {
            "snapshots" {
                if (repository.snapshots.enabled != null) {
                    "enabled" { -"${repository.snapshots.enabled}" }
                }
                if (repository.snapshots.updatePolicy != null) {
                    "updatePolicy" { -"${repository.snapshots.updatePolicy}" }
                }
            }
        }
    }
}
