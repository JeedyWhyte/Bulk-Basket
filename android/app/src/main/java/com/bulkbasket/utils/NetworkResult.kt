package com.bulkbasket.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(
        val message: String,
        val code: Int? = null,
    ) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extracts a human-readable error message from a failed response.
 * Understands the backend's `{"status": "error", "message": "..."}` envelope
 * as well as DRF validation errors like `{"field": ["problem"]}`, falling
 * back to [fallback] when the body is absent or unparsable.
 */
fun <T> Response<T>.errorMessage(fallback: String): String {
    val raw = try {
        errorBody()?.string()
    } catch (e: Exception) {
        null
    }
    if (raw.isNullOrBlank()) return fallback

    return try {
        val json = Gson().fromJson(raw, JsonObject::class.java)
        when {
            json.has("message") && json.get("message").isJsonPrimitive ->
                json.get("message").asString
            json.has("detail") && json.get("detail").isJsonPrimitive ->
                json.get("detail").asString
            else -> {
                // DRF field errors: {"field": ["msg", ...], ...}
                val parts = json.entrySet().mapNotNull { (key, value) ->
                    when {
                        value.isJsonArray && value.asJsonArray.size() > 0 ->
                            "$key: ${value.asJsonArray.first().asString}"
                        value.isJsonPrimitive -> "$key: ${value.asString}"
                        else -> null
                    }
                }
                if (parts.isEmpty()) fallback else parts.joinToString("\n")
            }
        }
    } catch (e: Exception) {
        fallback
    }
}