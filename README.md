# TariffCalculator API (Ktor + DynamoDB Local)

A Kotlin/Ktor REST API that calculates transport fares by passenger type. Each calculation is persisted to DynamoDB Local. The code follows a light hexagonal architecture and SOLID principles.

---

## Stack

* Kotlin 1.9+ · Ktor 2.x (ContentNegotiation, StatusPages, CallLogging)
* Gradle (Kotlin DSL)
* AWS SDK v2 (DynamoDbClient)
* DynamoDB Local (Docker)
* JUnit 5, Mockk (recommended for tests)

---

## Architecture

```
src/main/kotlin
├── domain
│   ├── model            # Journey, PassengerType, FareRequest, FareResult
│   └── service          # FareCalculator (port), FareCalculatorImpl, FareRepository (port)
├── infrastructure
│   ├── api              # Ktor routing (endpoints)
│   ├── config           # DatabaseConfig, DynamoBootstrap (creates table if missing)
│   └── db               # DynamoDBFareRepository (adapter for FareRepository)
└── Application.kt       # Ktor entrypoint (module)
```

* **Domain:** business logic and pure models.
* **Infrastructure:** HTTP API, database client, concrete repository.
* **DIP:** services depend on `FareRepository` (port), not on DynamoDB.

---

## Prerequisites

* JDK 21 (Amazon Corretto 21 recommended)
* Docker / Docker Compose
* Gradle Wrapper (`./gradlew`)
* (Optional) AWS CLI v2 for inspecting DynamoDB Local

---

## Run DynamoDB Local

Create `docker-compose.yml` at project root:

```yaml
services:
  dynamodb-local:
    image: amazon/dynamodb-local
    container_name: dynamodb-local
    ports:
      - "8000:8000"
    command: ["-jar", "DynamoDBLocal.jar", "-sharedDb", "-inMemory"]
```

Start it:

```bash
docker compose up -d dynamodb-local
docker ps  # should show 0.0.0.0:8000->8000
```

> Note: `-inMemory` wipes data on container restart. See “Persistence & Table” below for alternatives.

---

## Configure the application

The app reads the DynamoDB endpoint and dummy credentials from environment variables. Use alphanumeric values to avoid SDK validation errors.

**When running Ktor on your host (IntelliJ/Gradle):**

```bash
export DYNAMO_ENDPOINT=http://localhost:8000
export AWS_ACCESS_KEY_ID=AKIAFAKEACCESSKEYID12
export AWS_SECRET_ACCESS_KEY=FAKESECRETKEYFAKESECRETKEYFAKESECRETKEY12
```

**If you later run Ktor inside Docker Compose with DynamoDB:**

```bash
export DYNAMO_ENDPOINT=http://dynamodb-local:8000
```

---

## Run the API

```bash
./gradlew run
```

Health check:

```
GET http://localhost:8080/health
→ {"status":"OK"}
```

---

## Endpoints

### Calculate fare

```
POST /fares/calculate
Content-Type: application/json
```

**Request examples:**

```json
{ "origin": "Bogotá", "destination": "Medellín", "passengerType": "ADULT" }
```

```json
{ "origin": "Bogotá", "destination": "Medellín", "passengerType": "SENIOR", "age": 70 }
```

**Response example:**

```json
{ "price": 10.0, "message": "Success" }
```

Allowed `passengerType` values: `ADULT`, `CHILD`, `SENIOR`. For `SENIOR`, a discount applies when `age >= 65`.

---

## Persistence & Table

* **Table name:** `FareHistory`
* **Keys:**

  * `journeyId` (PK, `S`)
  * `createdAt` (SK, `S`)
* **Attributes:** `origin`, `destination`, `price`, `message`

### Bootstrap the table at app startup (recommended)

`DynamoBootstrap.ensureFareHistoryTable(client)` runs at startup and creates the table if it does not exist (useful with `-inMemory`).


