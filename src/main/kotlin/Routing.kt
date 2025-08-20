package com.example

import com.google.genai.Client
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun Application.configureRouting() {
    val modelVersion = "gemini-2.5-flash"
    val llmClient = Client.builder().apiKey(dotenv()["GOOGLE_API_KEY"]).build()

    routing {
        get("/") {
            val response = llmClient
                .async
                .models
                .generateContent(modelVersion, "Say hello", null)

            response.thenAccept {
                runBlocking {
                    call.respondText(it.text()!!)
                }
            }.join()
        }
    }
}
