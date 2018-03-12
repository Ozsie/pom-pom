package com.github.ozsie.pompom.model

data class Repository(val id: String?, val name: String, val url: String, val layout: String?,
                      val releases: RepositorySettings?, val snapshots: RepositorySettings?)

data class RepositorySettings(val enabled: Boolean?, val updatePolicy: String?)
