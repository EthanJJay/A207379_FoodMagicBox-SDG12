package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// 用户完整信息数据类
data class UserProfile(
    val username: String = "ZHANGJUNJIE",
    val userId: String = "A207379",
    val age: Int = 20,
    val gender: String = "Male",
    val residence: String = "Kajang, Malaysia"
)

// 收藏食物数据类
data class FoodItem(
    val name: String,
    val price: String,
    val imageRes: Int
)

class FoodViewModel : ViewModel() {

    // 用户信息状态管理
    var userProfile = mutableStateOf(UserProfile())
        private set

    // 收藏列表
    private val _favorites = mutableStateListOf<FoodItem>()
    val favorites: List<FoodItem> = _favorites

    // 更新用户信息（编辑页面使用）
    fun updateUserProfile(
        username: String,
        age: Int,
        gender: String,
        residence: String
    ) {
        userProfile.value = userProfile.value.copy(
            username = username,
            age = age,
            gender = gender,
            residence = residence
        )
    }

    // 收藏 / 取消收藏
    fun toggleFavorite(item: FoodItem) {
        if (_favorites.contains(item)) {
            _favorites.remove(item)
        } else {
            _favorites.add(item)
        }
    }

    fun isFavorite(item: FoodItem): Boolean {
        return _favorites.contains(item)
    }
}