package com.example.domain.service

import com.example.domain.model.FareResult
import com.example.domain.model.Journey
import com.example.domain.model.PassengerType
import com.example.infrastructure.db.DynamoDBFareRepository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class FareCalculatorImpl(private val repository: FareRepository) : FareCalculator {

    override fun calculateFare(journey: Journey, passengerType: PassengerType, age: Int): FareResult {
        if (journey.origin.isBlank() || journey.destination.isBlank()) {
            return FareResult(0.0, "Origin and destination have to be filled")
        }

        val baseFare = 10.0
        val fare = when (passengerType) {
            PassengerType.ADULT -> baseFare
            PassengerType.CHILD -> baseFare * 0.5
            PassengerType.SENIOR -> if (age >= 65) baseFare * 0.7 else baseFare
        }
        val result = FareResult(fare)
        repository.saveFare(journey, result)
        return FareResult(fare)
    }
}