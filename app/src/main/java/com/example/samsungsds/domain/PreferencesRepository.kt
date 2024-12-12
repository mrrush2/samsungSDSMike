package com.example.samsungsds.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository  @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore  by preferencesDataStore("settings")
    private val SWITCH_KEY = booleanPreferencesKey("switch_key")

    val switchFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SWITCH_KEY] ?: false
        }

    suspend fun saveSwitchState(isOn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SWITCH_KEY] = isOn
        }
    }
}