package com.github.ozsie.pompom.model

data class Plugin(var version: String?, val configuration: Map<String, Any>?,
                  val executions: Map<String, Execution>?, val dependencies: Map<String, String>?)

data class Execution(val phase: String?, val goals: List<String>?, val configuration: Map<String, Any>?)
