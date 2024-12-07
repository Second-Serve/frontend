package com.cs407.secondserve.model

enum class MapImageType(val type: String) {
    BANNER("banner"),
    ICON("icon");

    override fun toString() : String {
        return type
    }
}