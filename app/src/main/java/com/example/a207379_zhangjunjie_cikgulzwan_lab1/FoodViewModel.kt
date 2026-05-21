package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.app.Application
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 声明 DataStore 实例
private val Application.dataStore: DataStore<Preferences> by preferencesDataStore("user_profile")

data class UserProfile(
    val username: String = "ZHANGJUNJIE",
    val userId: String = "A207379",
    val age: Int = 20,
    val gender: String = "Male",
    val residence: String = "Kajang, Malaysia"
)

data class FoodItem(
    val name: String,
    val price: String,
    val imageRes: Int
)

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FoodRepository(database.foodDao())
    private val dataStore = application.dataStore

    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val USER_ID = stringPreferencesKey("user_id")
        val AGE = intPreferencesKey("age")
        val GENDER = stringPreferencesKey("gender")
        val RESIDENCE = stringPreferencesKey("residence")
    }

    val savedProfile: StateFlow<UserProfile> = dataStore.data
        .map { prefs ->
            UserProfile(
                username = prefs[Keys.USERNAME] ?: "ZHANGJUNJIE",
                userId = prefs[Keys.USER_ID] ?: "A207379",
                age = prefs[Keys.AGE] ?: 20,
                gender = prefs[Keys.GENDER] ?: "Male",
                residence = prefs[Keys.RESIDENCE] ?: "Kajang, Malaysia"
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        )

    val dbFavorites: StateFlow<List<FoodItem>> = repository.allFavorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveUserProfile(username: String, age: Int, gender: String, residence: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.USERNAME] = username
                prefs[Keys.AGE] = age
                prefs[Keys.GENDER] = gender
                prefs[Keys.RESIDENCE] = residence
            }
        }
    }

    fun toggleFavorite(item: FoodItem) {
        if (isFavorite(item)) {
            repository.delete(item)
        } else {
            repository.insert(item)
        }
    }

    fun isFavorite(item: FoodItem): Boolean {
        return dbFavorites.value.any { it.name == item.name }
    }
}