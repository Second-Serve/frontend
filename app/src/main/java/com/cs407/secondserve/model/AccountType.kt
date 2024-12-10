package com.cs407.secondserve.model

enum class AccountType(private val type: String) {
    CUSTOMER("customer"),
    BUSINESS("business");

    override fun toString() : String {
        return type
    }

    companion object {
        @OptIn(kotlin.ExperimentalStdlibApi::class)
        fun fromString(string: String) : AccountType {
            return entries.first {
                it.type == string.lowercase()
            }
        }
    }
}
