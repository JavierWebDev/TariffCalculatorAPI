package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FareRequest(
    val origin: String,
    val destination: String,
    val passengerType: PassengerType,
    val age: Int? = null
)
