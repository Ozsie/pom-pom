package com.github.ozsie.pompom.generator.xml

import com.github.ozsie.pompom.model.DistributionManagement
import com.github.ozsie.pompom.model.SCM
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun buildDistributionManagement(distributionManagement: DistributionManagement): Node = with(distributionManagement) {
    return xml("distributionManagement") {
        if (repository != null) {
            addNode(buildRepository("repository", repository.id!!, repository))
        }
    }
}

fun buildSCM(scm: SCM): Node = with(scm) {
    return xml("scm") {
        if (url != null) {
            "url" { -url }
        }
        if (connection != null) {
            "connection" { -connection }
        }
        if (developerConnection != null) {
            "developerConnection" { -developerConnection }
        }
        if (tag != null) {
            "tag" { -tag }
        }
    }
}

fun buildProperties(properties: Map<String, String>): Node = with(properties) {
    return xml("properties") {
        properties.forEach {
            it.key { -it.value }
        }
    }
}
