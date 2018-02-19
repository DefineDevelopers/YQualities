package net.webdefine.yq.delegates

object DelegatesExt {
    fun <T> notNullSingleValue() = NotNullSingleValueVar<T>()
}