package com.example

import com.example.domain.model.FareRequest
import com.example.domain.model.Journey
import com.example.domain.service.FareCalculatorImpl
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.plugins.calllogging.*
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        )
    }

    // Logging de cada request
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
        filter { true } // loguea todas las llamadas
    }

    // Manejo de errores global
    install(io.ktor.server.plugins.statuspages.StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace() // imprime el stacktrace completo en consola
            call.respondText(
                "Error: ${cause.message}",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )
        }
    }

    routing {
        get("/") {
            call.respondText("Hello hola!")
        }

        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        post("/fares/calculate") {
            try {
                val request = call.receive<FareRequest>()
                val calculator = FareCalculatorImpl()
                val result = calculator.calculateFare(
                    Journey(request.origin, request.destination),
                    request.passengerType,
                    request.age ?: 0
                )
                call.respond(result)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText(
                    "Error processing request: ${e.message}",
                    status = io.ktor.http.HttpStatusCode.BadRequest
                )
            }
        }
    }
}

