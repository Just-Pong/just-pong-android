package edu.sapi.justpongapp.backend.models

data class MovementData(val x: Double, val y: Double, val z: Double): BaseEntity() {
    val timestampInMillis = System.currentTimeMillis();
    val timestamp = timestampInMillis / 1000;

    override fun toJson(): String {
        return gson.toJson(this)
    }
}
