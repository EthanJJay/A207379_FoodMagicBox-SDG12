package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// =========================================================
// 核心数据类模型定义 (Core Data Class Models)
// =========================================================

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

data class FirebaseBooking(
    val id: String = "",         // 以取餐码作为该条云端数据的 documentId 主键
    val storeName: String = "",
    val orderTime: String = "",
    val pickupCode: String = "",
    val status: String = "Confirmed"
)

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    // 💡 绝杀防御：使用 lazy 延迟加载，并在获取实例前强行塞入安全初始化检查，彻底消灭初始化闪退！
    val firestore: FirebaseFirestore by lazy {
        ensureFirebaseInitialized(application)
        FirebaseFirestore.getInstance()
    }

    private fun ensureFirebaseInitialized(context: Application) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyCdsmE0c2BzrqXpd2bZb-ZtUisCyeTUsJ8")
                    .setApplicationId("1:633288661790:android:26d92e3435ba9d3e24c70f")
                    .setProjectId("sdg12-foodmagicbox")
                    .setStorageBucket("sdg12-foodmagicbox.firebasestorage.app")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val database = AppDatabase.getDatabase(application)
    private val repository = FoodRepository(database.foodDao())

    // 默认检索主学号下的个人资料
    private val targetUserId = "A207379"

    // 1. 本地 Room 持久化：实时同步并对外吐出最新的个人资料数据流
    val savedProfile: StateFlow<UserProfile> = repository.getUserProfile(targetUserId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        )

    // 2. 本地 Room 持久化：实时同步并对外吐出最新的“我的收藏”数据流
    val dbFavorites: StateFlow<List<FoodItem>> = repository.allFavorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. 业务功能：本地持久化保存个人资料修改
    fun saveUserProfile(username: String, age: Int, gender: String, residence: String) {
        viewModelScope.launch {
            val updatedProfile = UserProfile(
                username = username,
                userId = targetUserId,
                age = age,
                gender = gender,
                residence = residence
            )
            repository.saveUserProfile(updatedProfile)
        }
    }

    // 4. 业务功能：控制红心卡片的收藏与擦除
    fun toggleFavorite(item: FoodItem) {
        viewModelScope.launch {
            if (isFavorite(item)) {
                repository.delete(item)
            } else {
                repository.insert(item)
            }
        }
    }

    fun isFavorite(item: FoodItem): Boolean {
        return dbFavorites.value.any { it.name == item.name }
    }

    // =========================================================
    // 5. 核心拓展：Firebase Firestore 云端订单双向控制流 (Cloud Sync)
    // =========================================================

    /**
     * 将预订订单实时永久推送到 Firebase 云数据库 (Confirmed State)
     */
    fun pushBookingToCloud(storeName: String, orderTime: String, pickupCode: String) {
        val bookingData = FirebaseBooking(
            id = pickupCode,
            storeName = storeName,
            orderTime = orderTime,
            pickupCode = pickupCode,
            status = "Confirmed"
        )

        firestore.collection("bookings")
            .document(pickupCode)
            .set(bookingData)
    }

    /**
     * 将预订订单从 Firebase 远程云端直接彻底抹去 (Cancel State)
     */
    fun deleteBookingFromCloud(pickupCode: String, onSuccess: () -> Unit) {
        firestore.collection("bookings")
            .document(pickupCode)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
    }
}