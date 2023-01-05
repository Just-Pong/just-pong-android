package edu.sapi.justpongapp.backend.models

abstract class BaseEntity {
    abstract fun toJson(): String
}