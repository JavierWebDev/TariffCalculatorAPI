package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FareResult(
    val price: Double,
    val message : String = "Success"
)
