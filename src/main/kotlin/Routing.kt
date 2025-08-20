package com.example

import com.google.genai.Client
import io.github.cdimascio.dotenv.dotenv
import io.github.lm98.whdt.core.hdt.model.property.Properties.mood
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun Application.configureRouting() {
    val modelVersion = "gemini-2.5-flash"
    val llmClient = Client.builder().apiKey(dotenv()["GOOGLE_API_KEY"]).build()

    routing {
        get("api/hdt/{id}/update/mood/{systolic}/{diastolic}") {

            val dtId = call.parameters["id"] ?: return@get call.respondText(
                "Missing Digital Twin ID", status = HttpStatusCode.BadRequest
            )

            val systolic = call.parameters["systolic"] ?: return@get call.respondText(
                "Missing Systolic Blood Pressure", status = HttpStatusCode.BadRequest
            )

            val diastolic = call.parameters["diastolic"] ?: return@get call.respondText(
                "Missing Diastolic Blood Pressure", status = HttpStatusCode.BadRequest
            )

            val prompt = getPrompt(systolic.toInt(), diastolic.toInt())

            val response = llmClient
                .async
                .models
                .generateContent(modelVersion, prompt, null)

            response.thenAccept { res ->
                val raw = res.text()
                val mood = mood(raw!!.toInt())
                runBlocking {
                    call.respond(mood)
                }
            }.join()
        }
    }
}

fun getPrompt(systolic: Int, diastolic: Int): String {
    return """
        Respond only with a integer numerical value from 0 to 100. 
        What is my current mood score given a systolic blood pressure of $systolic and a diastolic 
        blood pressure of $diastolic ?
    """.trimIndent()
}