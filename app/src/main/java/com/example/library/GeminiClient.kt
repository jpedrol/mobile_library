package com.example.library

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class GeminiClient(
    private val apiKey: String
) {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    data class GeminiPart(val text: String)
    data class GeminiContent(val parts: List<GeminiPart>)
    data class GeminiRequest(val contents: List<GeminiContent>)

    data class GeminiCandidate(val content: GeminiContent?)
    data class GeminiResponse(val candidates: List<GeminiCandidate>?)

    fun sendMessageBlocking(prompt: String): String {
        val bodyObj = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        val bodyJson = gson.toJson(bodyObj)
        val requestBody = bodyJson.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return "Erro na API: ${response.code} - $responseBody"
            }

            val geminiResponse = gson.fromJson(responseBody, GeminiResponse::class.java)

            val text = geminiResponse
                .candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

            return text ?: "Não foi possível entender a resposta."
        }
    }
}
