package com.github.ozsie.pompom.generator.xml

import com.github.ozsie.pompom.model.DistributionManagement
import com.github.ozsie.pompom.model.SCM
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun buildDistributionManagement(distributionManagement: DistributionManagement): Node = with(distributionManagement) {
    return xml("distributionManagement") {
        if (repository != null) {
            buildRepository("repository", repository.id!!, repository)
        }
    }
}

fun buildSCM(scm: SCM): Node = with(scm) {
    return xml("scm") {
        "url" { -url }
        "connection" { -connection }
        "developerConnection" { -developerConnection}
        "tag" { -tag }
    }
}

fun buildProperties(properties: Map<String, String>): Node = with(properties) {
    return xml("properties") {
        properties.forEach {
            it.key { -it.value }
        }
    }
}
