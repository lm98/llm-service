package com.example

import io.github.lm98.whdt.core.serde.Stub
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Stub.hdtJson)
    }
}
