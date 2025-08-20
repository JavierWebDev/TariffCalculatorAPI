// infrastructure/db/DynamoDBFareRepository.kt
package com.example.infrastructure.db

import com.example.domain.model.FareResult
import com.example.domain.model.Journey
import com.example.domain.service.FareRepository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Instant
import java.util.UUID

class DynamoDBFareRepository(private val dynamo: DynamoDbClient) : FareRepository {
    private val tableName = "FareHistory"

    override fun saveFare(journey: Journey, fareResult: FareResult) {
        val item = mapOf(
            "journeyId" to AttributeValue.builder().s(UUID.randomUUID().toString()).build(), // PK
            "createdAt" to AttributeValue.builder().s(Instant.now().toString()).build(),     // SK
            "origin" to AttributeValue.builder().s(journey.origin).build(),
            "destination" to AttributeValue.builder().s(journey.destination).build(),
            "price" to AttributeValue.builder().n(fareResult.price.toString()).build(),
            "message" to AttributeValue.builder().s(fareResult.message).build()
        )

        val req = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        println(">> PutItem en $tableName (endpoint OK)")
        dynamo.putItem(req)
    }
}
