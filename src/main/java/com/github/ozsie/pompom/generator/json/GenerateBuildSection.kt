package com.github.ozsie.pompom.generator.json

import com.github.ozsie.pompom.generator.getText
import com.github.ozsie.pompom.model.Build
import com.github.ozsie.pompom.model.Execution
import com.github.ozsie.pompom.model.Plugin
import org.redundent.kotlin.xml.Node

fun Node.getBuild(): Build? {
    var finalName: String? = null
    var plugins: HashMap<String, Plugin> = HashMap()
    children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "finalName" -> finalName = it.getText()
            "plugins" -> plugins = it.getPlugins()
        }
    }

    return Build(finalName, plugins, null)
}

private fun Node.getPlugins(): HashMap<String, Plugin> {
    var plugins: HashMap<String, Plugin> = HashMap()
    children.filter { it is Node && it.nodeName == "plugin" }.forEach { it as Node
        plugins.putAll(it.getPlugin())
    }

    return plugins
}

private fun Node.getPlugin(): HashMap<String, Plugin> {
    var mappedPlugin: HashMap<String, Plugin> = HashMap()
    var groupId: String? = null
    var artifactId: String? = null
    var version: String? = null
    var configuration: HashMap<String, Any>? = HashMap()
    var executions: HashMap<String, Execution>? = HashMap()
    var dependenciesAny: Map<String, Any>? = HashMap()
    children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "groupId" -> groupId = it.getText()
            "artifactId" -> artifactId = it.getText()
            "version" -> version = it.getText()
            "configuration" -> configuration = it.getConfiguration()
            "executions" -> executions = it.getExecutions()
            "dependencies" -> dependenciesAny = it.getDependencies()
        }
    }


    val dependencies = HashMap<String, String>()
    dependenciesAny?.forEach { key, value ->
        dependencies.put(key, value as String)
    }

    mappedPlugin.put("$groupId:$artifactId", Plugin(version, configuration, executions, dependencies))

    return mappedPlugin
}

private fun Node.getExecutions(): HashMap<String, Execution> {
    var executions: HashMap<String, Execution> = HashMap()

    children.filter { it is Node && it.nodeName == "execution"}.forEach { it as Node
        executions.putAll(it.getExecution())
    }

    return executions
}

private fun Node.getExecution(): HashMap<String, Execution> {
    var mappedExecution: HashMap<String, Execution> = HashMap()
    var id = ""
    var phase: String? = ""
    var goals: ArrayList<String> = ArrayList()
    var configuration: HashMap<String, Any>? = HashMap()
    children.filter { it is Node }.forEach { it as Node
        when (it.nodeName) {
            "id" -> id = it.getText()
            "phase" -> phase = it.getText()
            "goals" -> (it.children.filter { it is Node && it.nodeName == "goal" }[0] as Node).getText()
            "configuration" -> configuration = it.getConfiguration()
        }
    }

    mappedExecution.put(id, Execution(phase, goals, configuration))

    return mappedExecution
}

private fun Node.getConfiguration(): HashMap<String, Any>? {
    var configuration: HashMap<String, Any>? = HashMap()



    return configuration
}