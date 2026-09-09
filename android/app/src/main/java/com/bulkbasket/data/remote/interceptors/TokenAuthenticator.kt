package com.bulkbasket.data.remote.interceptors

import com.bulkbasket.utils.Constants
import com.bulkbasket.utils.PreferencesManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Refreshes the JWT access token when a request comes back 401.
 *
 * Uses its own bare OkHttp client so the refresh call is not itself
 * intercepted/authenticated (which would loop). If the refresh token is
 * missing or rejected, stored credentials are cleared so the next launch
 * lands on the Login screen instead of an endless string of 401s.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val prefs: PreferencesManager,
) : Authenticator {

    private val refreshClient = OkHttpClient()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Give up after one retry per call chain.
        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking { prefs.refreshToken.first() }
        if (refreshToken.isEmpty()) return null

        val newAccess = synchronized(this) {
            // Another thread may have refreshed while we waited on the lock.
            val current = runBlocking { prefs.accessToken.first() }
            val failedWith = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
            if (current.isNotEmpty() && current != failedWith) {
                current
            } else {
                requestNewAccessToken(refreshToken)
            }
        } ?: run {
            // Refresh token rejected — force re-login.
            runBlocking { prefs.clear() }
            return null
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun requestNewAccessToken(refreshToken: String): String? {
        return try {
            val body = Gson().toJson(mapOf("refresh" to refreshToken))
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("${Constants.BASE_URL}users/token/refresh/")
                .post(body)
                .build()
            refreshClient.newCall(request).execute().use { res ->
                if (!res.isSuccessful) return null
                val json = Gson().fromJson(
                    res.body?.string(), JsonObject::class.java
                ) ?: return null
                val access = json.get("access")?.asString ?: return null
                val rotatedRefresh = json.get("refresh")?.asString ?: refreshToken
                runBlocking { prefs.saveTokens(access, rotatedRefresh) }
                access
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
