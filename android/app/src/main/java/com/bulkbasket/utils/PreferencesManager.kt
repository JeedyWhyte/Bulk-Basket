package com.bulkbasket.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("bulkbasket_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val accessToken: Flow<String> = context.dataStore.data
        .map { it[ACCESS_TOKEN] ?: "" }

    val userRole: Flow<String> = context.dataStore.data
        .map { it[USER_ROLE] ?: "" }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveRole(role: String) {
        context.dataStore.edit { it[USER_ROLE] = role }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}