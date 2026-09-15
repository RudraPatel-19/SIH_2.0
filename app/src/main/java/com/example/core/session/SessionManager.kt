package com.example.core.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kisan_session_prefs")

/**
 * Manages user session state, authentication tokens, and credentials securely using DataStore.
 */
class SessionManager(private val context: Context) {

  val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[KEY_IS_LOGGED_IN] ?: false
  }

  val currentUserEmail: Flow<String> = context.dataStore.data.map { preferences ->
    preferences[KEY_USER_EMAIL] ?: ""
  }

  val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[KEY_ONBOARDING_COMPLETED] ?: false
  }

  val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[KEY_AUTH_TOKEN]
  }

  suspend fun getAuthToken(): String? {
    return context.dataStore.data.map { preferences -> preferences[KEY_AUTH_TOKEN] }.firstOrNull()
  }

  suspend fun setOnboardingCompleted() {
    context.dataStore.edit { preferences ->
      preferences[KEY_ONBOARDING_COMPLETED] = true
    }
  }

  suspend fun saveSession(email: String, token: String) {
    context.dataStore.edit { preferences ->
      preferences[KEY_IS_LOGGED_IN] = true
      preferences[KEY_USER_EMAIL] = email
      preferences[KEY_AUTH_TOKEN] = token
    }
  }

  suspend fun clearSession() {
    context.dataStore.edit { preferences ->
      preferences.remove(KEY_IS_LOGGED_IN)
      preferences.remove(KEY_USER_EMAIL)
      preferences.remove(KEY_AUTH_TOKEN)
    }
  }

  companion object {
    private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
  }
}
