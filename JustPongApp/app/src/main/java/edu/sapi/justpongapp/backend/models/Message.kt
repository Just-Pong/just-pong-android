package edu.sapi.justpongapp.backend.models

data class Message(private val message: String): BaseEntity() {
    override fun toJson(): String {
        return gson.toJson(this)
    }
}