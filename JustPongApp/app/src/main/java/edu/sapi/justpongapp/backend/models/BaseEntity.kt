package edu.sapi.justpongapp.backend.models

import com.google.gson.Gson

abstract class BaseEntity {
    protected val gson = Gson()

    abstract fun toJson(): String
}