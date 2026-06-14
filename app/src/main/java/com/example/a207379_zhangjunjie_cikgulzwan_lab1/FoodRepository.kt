package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 仓储层实现类 (FoodRepository)
 */
class FoodRepository(private val foodDao: FoodDao) {

    // 将底层的 FoodEntity 转换为 UI 层直接读取的 FoodItem
    val allFavorites: Flow<List<FoodItem>> = foodDao.getAllFavorites().map { entities ->
        entities.map { FoodItem(it.name, it.price, it.imageRes) }
    }

    suspend fun insert(foodItem: FoodItem) {
        foodDao.insertFavorite(FoodEntity(foodItem.name, foodItem.price, foodItem.imageRes))
    }

    suspend fun delete(foodItem: FoodItem) {
        foodDao.deleteFavorite(FoodEntity(foodItem.name, foodItem.price, foodItem.imageRes))
    }

    // 获取本地持久化个人资料，若为空则映射返回默认初值
    fun getUserProfile(userId: String): Flow<UserProfile> {
        return foodDao.getUserProfileById(userId).map { entity ->
            if (entity != null) {
                UserProfile(entity.username, entity.userId, entity.age, entity.gender, entity.residence)
            } else {
                UserProfile() // 默认兜底初值
            }
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        foodDao.insertUserProfile(
            UserEntity(
                userId = userProfile.userId,
                username = userProfile.username,
                age = userProfile.age,
                gender = userProfile.gender,
                residence = userProfile.residence
            )
        )
    }
}