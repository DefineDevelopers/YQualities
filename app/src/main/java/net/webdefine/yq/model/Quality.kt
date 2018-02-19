package net.webdefine.yq.model

data class Quality(val uuid: String,
                   val profile: Int,
                   val name: String,
                   val iconName: String,
                   val primaryColor: String,
                   val accentColor: String,
                   val value: Int)