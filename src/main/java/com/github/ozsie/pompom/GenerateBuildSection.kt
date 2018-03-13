package com.github.ozsie.pompom

import com.github.ozsie.pompom.model.Build
import com.github.ozsie.pompom.model.Execution
import com.github.ozsie.pompom.model.Plugin
import com.github.ozsie.pompom.plugins.buildPluginForMvnCompiler
import com.github.ozsie.pompom.plugins.buildPluginsForExecutableKotlin
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun String.asResource(work: (String) -> Unit) {
    val content = this.javaClass::class.java.getResource(this).readText()
    work(content)
}

fun getPluginAdapter(): JsonAdapter<Plugin> {
    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    return moshi.adapter(Plugin::class.java)
}

fun buildBuild(build: Build, artifactId: String): Node {
    return xml("build") {
        var finalName = artifactId
        if (build.finalName != null) {
            finalName = build.finalName
            "finalName" { -"${build.finalName}" }
        }
        var pluginsNode: Node? = null
        if (build.plugins != null) {
            pluginsNode = xml("plugins") {
                build.plugins.forEach {
                    val key = it.key
                    val value = it.value
                    addNode(buildPlugin(key, value))
                }
            }
            addNode(pluginsNode)
        }
        if (build.pomPomPlugins != null) {
            if (pluginsNode == null) {
                pluginsNode = xml("plugins")
            }
            build.pomPomPlugins.forEach {
                val key = it.key
                val value = it.value
                when(key) {
                    "\$kotlin.executable" -> {
                        pluginsNode?.buildPluginsForExecutableKotlin(value, finalName)
                    }
                    "\$mvn.compiler" -> {
                        pluginsNode?.buildPluginForMvnCompiler(value)
                    }
                }
            }
        }
    }
}

fun buildPlugin(artifact: String, plugin: Plugin): Node {
    return xml("plugin") {
        "groupId" { -artifact.substringBefore(":") }
        "artifactId" { -artifact.substringAfter(":") }
        if (plugin.version != null) {
            "version" { -"${plugin.version}" }
        }
        val configuration = plugin.configuration
        if (configuration != null) {
            "configuration" {
                configuration.keys.forEach {
                    addNode(buildConfiguration(it, configuration[it], this))
                }
            }
        }
        val executions = plugin.executions
        if (executions != null) {
            "executions" {
                executions.keys.forEach {
                    val execution = executions[it]
                    if (execution != null) {
                        addNode(buildExecution(it, execution))
                    }
                }
            }
        }
        val dependencies = plugin.dependencies
        if (dependencies != null) {
            addNode(buildDependencies(dependencies))
        }
    }
}

fun buildConfiguration(key: String, value: Any?, parent: Node?): Node {
    when(value) {
        is Map<*,*> -> {
            var node: Node? = parent
            if (key == "\$repeat" && parent != null) {
                val entries = value["\$entries"] as List<*>
                entries.forEach {
                    buildRepeat(it, value, parent)
                }
            } else {
                val subConf = xml(key)
                value.forEach {
                    buildSubConfiguration(it, subConf)
                }
                node = subConf
            }
            return node!!
        }
        else -> return xml(key) { -"$value" }
    }
}

fun buildSubConfiguration(it: Map.Entry<Any?, Any?>, subConf: Node) {
    val subKey = it.key.toString()
    val attributes = it.value
    if (subKey == "\$attributes" && attributes is List<*>) {
        attributes.forEach {
            if (it is Map<*, *> && it.size == 1) {
                val attrKey = it.keys.first() as String
                val attrValue = it.values.first() as String
                subConf.attribute(attrKey, attrValue)
            }
        }
    } else {
        val node = buildConfiguration(it.key.toString(), it.value, subConf)
        if (node != subConf) {
            subConf.addNode(node)
        }
    }
}

fun buildRepeat(it: Any?, value: Map<*, *>, parent: Node) {
    val entry = it as Map<*, *>
    val attributeList = entry["\$attributes"] as List<*>
    val children = entry["\$children"]
    val subConf = xml(value["\$key"] as String) {
        attributeList.forEach {
            val attribute = it as Map<*, *>
            val attrKey = attribute.keys.first() as String
            val attrValue = attribute.values.first() as String
            attribute(attrKey, attrValue)
        }
        when (children) {
            is String -> {
                -children
            }
            is Map<*, *> -> {
                children.forEach {
                    addNode(buildConfiguration(it.key as String, it.value, this))
                }
            }
        }
    }
    parent.addNode(subConf)
}

fun buildExecution(id: String, execution: Execution): Node = with(execution) {
    return xml("execution") {
        "id" { -id }
        if (phase != null) {
            "phase" { -phase }
        }
        if (goals != null) {
            "goals" {
                goals.forEach {
                    "goal" { -it }
                }
            }
        }
        val configuration = configuration
        if (configuration != null) {
            "configuration" {
                configuration.keys.forEach {
                    addNode(buildConfiguration(it, configuration[it], this))
                }
            }
        }
    }
}
