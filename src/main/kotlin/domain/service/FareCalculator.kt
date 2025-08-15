package com.example.domain.service

import com.example.domain.model.FareResult
import com.example.domain.model.Journey
import com.example.domain.model.PassengerType

interface FareCalculator {
    fun calculateFare(journey : Journey, passengerType: PassengerType, age : Int = 0) : FareResult
}