package com.bulkbasket.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("bulkbasket_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFY_ORDER_UPDATES = booleanPreferencesKey("notify_order_updates")
        val NOTIFY_PROMOTIONS = booleanPreferencesKey("notify_promotions")
        val NOTIFY_NEW_ARRIVALS = booleanPreferencesKey("notify_new_arrivals")
    }

    val accessToken: Flow<String> = context.dataStore.data
        .map { it[ACCESS_TOKEN] ?: "" }

    val refreshToken: Flow<String> = context.dataStore.data
        .map { it[REFRESH_TOKEN] ?: "" }

    val userRole: Flow<String> = context.dataStore.data
        .map { it[USER_ROLE] ?: "" }

    val username: Flow<String> = context.dataStore.data
        .map { it[USERNAME] ?: "" }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUserInfo(role: String, username: String, userId: String) {
        context.dataStore.edit {
            it[USER_ROLE] = role
            it[USERNAME] = username
            it[USER_ID] = userId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }


    val themeMode: Flow<String> = context.dataStore.data
        .map { it[THEME_MODE] ?: "system" }

    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    val notifyOrderUpdates: Flow<Boolean> = context.dataStore.data
        .map { it[NOTIFY_ORDER_UPDATES] ?: true }
    val notifyPromotions: Flow<Boolean> = context.dataStore.data
        .map { it[NOTIFY_PROMOTIONS] ?: true }
    val notifyNewArrivals: Flow<Boolean> = context.dataStore.data
        .map { it[NOTIFY_NEW_ARRIVALS] ?: true }

    suspend fun setNotifyOrderUpdates(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_ORDER_UPDATES] = enabled }
    }
    suspend fun setNotifyPromotions(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_PROMOTIONS] = enabled }
    }
    suspend fun setNotifyNewArrivals(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_NEW_ARRIVALS] = enabled }
    }
}
