package edu.sapi.justpongapp.backend.models

import com.google.gson.Gson

data class MovementData(val position: Double): BaseEntity() {
//    private val timestampInMillis = System.currentTimeMillis();
//    val timestamp = timestampInMillis / 1000;

    override fun toJson(): String {
        return Gson().toJson(this)
    }
}
