package com.github.ozsie.pompom.model

data class Pom (val modelVersion: String = "4.0.0", val artifact: String,
                val version: String, val packaging: String = "jar",
                val properties: Map<String, String>?, val dependencies: Map<String, Any>?,
                val build: Build?, val pluginRepositories: Map<String, Repository>?,
                val scm: SCM?, val distributionManagement: DistributionManagement?,
                val repositories: Map<String, Repository>?) {

    override fun toString(): String {
        return "$artifact:$version -> $packaging\n\n" +
                "$properties\n\n" +
                "$dependencies\n\n" +
                "$pluginRepositories\n\n" +
                "$scm\n\n" +
                "$distributionManagement\n\n"
    }

    val groupId: String
        get() {
            return artifact.substringBefore(":")
        }

    val artifactId: String
        get() {
            return artifact.substringAfter(":")
        }
}
