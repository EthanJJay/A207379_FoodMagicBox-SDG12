package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// =========================================================
// 核心数据类模型定义 (Core Data Class Models) - 补回这里就不会报红了！
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

    private val database = AppDatabase.getDatabase(application)
    private val repository = FoodRepository(database.foodDao())

    // 初始化 Firebase Firestore 远程联机实例 [cite: 9, 21]
    private val firestore = FirebaseFirestore.getInstance()

    // 默认检索主学号下的个人资料
    private val targetUserId = "A207379"

    // 1. 本地 Room 持久化：实时同步并对外吐出最新的个人资料数据流 [cite: 20]
    val savedProfile: StateFlow<UserProfile> = repository.getUserProfile(targetUserId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        )

    // 2. 本地 Room 持久化：实时同步并对外吐出最新的“我的收藏”数据流 [cite: 20]
    val dbFavorites: StateFlow<List<FoodItem>> = repository.allFavorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. 业务功能：本地持久化保存个人资料修改 [cite: 20]
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
    // 5. 核心拓展：Firebase Firestore 云端订单双向控制流 (Cloud Sync) [cite: 9, 21]
    // =========================================================

    /**
     * 将预订订单实时永久推送到 Firebase 云数据库 (Confirmed State) [cite: 9, 21]
     */
    fun pushBookingToCloud(storeName: String, orderTime: String, pickupCode: String) {
        val bookingData = FirebaseBooking(
            id = pickupCode, // 使用六位随机取餐码作为唯一文档 ID
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
     * 将预订订单从 Firebase 远程云端直接彻底抹去 (Cancel State) [cite: 9, 21]
     */
    fun deleteBookingFromCloud(pickupCode: String, onSuccess: () -> Unit) {
        firestore.collection("bookings")
            .document(pickupCode)
            .delete()
            .addOnSuccessListener {
                onSuccess() // 云端数据彻底不存在后，触发本地 UI 刷新回调
            }
    }
}