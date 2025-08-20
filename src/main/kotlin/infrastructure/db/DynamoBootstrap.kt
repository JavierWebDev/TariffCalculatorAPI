// infrastructure/config/DynamoBootstrap.kt
package com.example.infrastructure.config

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

object DynamoBootstrap {
    fun ensureFareHistoryTable(dynamo: DynamoDbClient, tableName: String = "FareHistory") {
        val exists = dynamo.listTables().tableNames().contains(tableName)
        if (exists) return

        val req = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(
                KeySchemaElement.builder().attributeName("journeyId").keyType(KeyType.HASH).build(),
                KeySchemaElement.builder().attributeName("createdAt").keyType(KeyType.RANGE).build()
            )
            .attributeDefinitions(
                AttributeDefinition.builder().attributeName("journeyId").attributeType(ScalarAttributeType.S).build(),
                AttributeDefinition.builder().attributeName("createdAt").attributeType(ScalarAttributeType.S).build()
            )
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build()

        dynamo.createTable(req)

        repeat(20) {
            val status = dynamo.describeTable(
                DescribeTableRequest.builder().tableName(tableName).build()
            ).table().tableStatus()
            if (status == TableStatus.ACTIVE) return
            Thread.sleep(200)
        }
    }
}
