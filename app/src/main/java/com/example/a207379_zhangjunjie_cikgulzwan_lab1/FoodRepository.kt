package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 仓储层实现类 (FoodRepository) [cite: 15, 52]
 */
class FoodRepository(private val foodDao: FoodDao) {

    // 将底层的 FoodEntity 转换为 UI 层直接读取的 FoodItem
    val allFavorites: Flow<List<FoodItem>> = foodDao.getAllFavorites().map { entities ->
        entities.map { FoodItem(it.name, it.price, it.imageRes) }
    }

    fun insert(foodItem: FoodItem) {
        foodDao.insertFavorite(FoodEntity(foodItem.name, foodItem.price, foodItem.imageRes))
    }

    fun delete(foodItem: FoodItem) {
        foodDao.deleteFavorite(FoodEntity(foodItem.name, foodItem.price, foodItem.imageRes))
    }
}