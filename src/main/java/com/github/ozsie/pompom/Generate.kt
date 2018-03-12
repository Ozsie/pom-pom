package com.github.ozsie.pompom

import com.github.ozsie.pompom.model.DistributionManagement
import com.github.ozsie.pompom.model.Pom
import com.github.ozsie.pompom.model.SCM
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okio.Okio
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml
import java.io.File
import java.io.InputStream

fun generate(pomFile: String): String {
    val adapter = getPomAdapter()
    val inputStream: InputStream = File(pomFile).inputStream()
    val pom = adapter.fromJson(Okio.buffer(Okio.source(inputStream)))

    pom?: throw NullPomException("Pom should not be null")

    with (pom) {
        val pomXml = xml("project") {
            xmlns = "http://maven.apache.org/POM/4.0.0"
            attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
            attribute("xsi:schemaLocation",
                    "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd")
            "modelVersion" { -modelVersion }
            "groupId" { -groupId }
            "artifactId" { -artifactId }
            "version" { -version }
            "packaging" { -packaging }
        }
        if (properties != null) {
            pomXml.addNode(buildProperties(properties))
        }
        if (dependencies != null) {
            pomXml.addNode(buildDependencies(dependencies))
        }
        if (build != null) {
            pomXml.addNode(buildBuild(build))
        }
        if (pluginRepositories != null) {
            pomXml.addNode(buildRepositories("pluginRepositories", pluginRepositories))
        }
        if (repositories != null) {
            pomXml.addNode(buildRepositories("repositories", repositories))
        }
        if(scm != null) {
            pomXml.addNode(buildSCM(scm))
        }
        if(distributionManagement != null) {
            pomXml.addNode(buildDistributionManagement(distributionManagement))
        }
        return pomXml.toString()
    }
}

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

fun getPomAdapter(): JsonAdapter<Pom> {
    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    return moshi.adapter(Pom::class.java)
}
