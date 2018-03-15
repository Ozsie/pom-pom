package com.github.ozsie.pompom.generator

import com.github.ozsie.pompom.NullPomException
import com.github.ozsie.pompom.generator.json.*
import com.github.ozsie.pompom.generator.xml.*
import com.github.ozsie.pompom.model.DistributionManagement
import com.github.ozsie.pompom.model.Pom
import com.github.ozsie.pompom.model.Repository
import com.github.ozsie.pompom.model.SCM
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okio.Okio
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.parse
import org.redundent.kotlin.xml.xml
import java.io.File
import java.io.InputStream

fun generateJson(xmlFile: String): String {
    File(xmlFile).inputStream().use {
        val xml = parse(it)

        var modelVersion = "4.0.0"
        var artifactId = ""
        var groupId = ""
        var version = ""
        var packaging = "jar"
        var properties: Map<String,String>? = HashMap<String, String>()
        var dependencies: Map<String, Any>? = HashMap<String, Any>()
        var pluginRepositories: Map<String, Repository>? = HashMap<String, Repository>()
        var repositories: Map<String, Repository>? = HashMap<String, Repository>()
        var scm: SCM? = null
        var distributionManagement: DistributionManagement? = null
        for (element in xml.children) {
            if (element is Node) {
                when (element.nodeName) {
                    "modelVersion" -> modelVersion = element.getText()
                    "artifactId" -> artifactId = element.getText()
                    "groupId" -> groupId = element.getText()
                    "version" -> version = element.getText()
                    "packaging" -> packaging = element.getText()
                    "properties" -> properties = element.getProperties()
                    "dependencies" -> dependencies = element.getDependencies()
                    "build" -> {}
                    "pluginRepositories" -> pluginRepositories = element.getRepositories("pluginRepository")
                    "scm" -> scm = element.getSCM()
                    "distributionManagement" -> distributionManagement = element.getDistributionManagement()
                    "repositories" -> repositories = element.getRepositories("repository")
                }
            }
        }
        val pom = Pom(modelVersion, "$groupId:$artifactId", version, packaging, properties, dependencies,
                null, pluginRepositories, scm, distributionManagement, repositories)
        return getPomAdapter().toJson(pom)
    }
}

fun generateXml(pomFile: String): String {
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
            pomXml.addNode(buildBuild(build, artifactId))
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

fun getPomAdapter(): JsonAdapter<Pom> {
    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    return moshi.adapter(Pom::class.java)
}
