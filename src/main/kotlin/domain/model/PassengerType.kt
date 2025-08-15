package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PassengerType {
    ADULT, CHILD, SENIOR
}