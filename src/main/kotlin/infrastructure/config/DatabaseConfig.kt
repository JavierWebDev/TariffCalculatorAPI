// infrastructure/config/DatabaseConfig.kt
package com.example.infrastructure.config

import com.example.domain.service.FareRepository
import com.example.infrastructure.db.DynamoDBFareRepository
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

object DatabaseConfig {
    fun createFareRepository(): FareRepository {
        // Si Kotlin corre en tu máquina: http://localhost:8000
        // Si metes Kotlin en Docker Compose: http://dynamodb-local:8000
        val endpoint = System.getenv("DYNAMO_ENDPOINT") ?: "http://localhost:8000"
        val accessKey = System.getenv("AWS_ACCESS_KEY_ID") ?: "AKIAFAKEACCESSKEYID12"
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY") ?: "FAKESECRETKEYFAKESECRETKEYFAKESECRETKEY12"

        println(">> DYNAMO_ENDPOINT (efectivo) = $endpoint")

        val client = DynamoDbClient.builder()
            .endpointOverride(URI(endpoint))
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build()

        // Asegura la existencia de la tabla SIEMPRE que arranca la app
        DynamoBootstrap.ensureFareHistoryTable(client)

        return DynamoDBFareRepository(client)
    }
}
