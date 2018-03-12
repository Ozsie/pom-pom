package com.github.ozsie.pompom.model

data class Build(val finalName: String?, val plugins: Map<String, Plugin>?,
                 val pomPomPlugins: Map<String, Map<String, String>>?)
