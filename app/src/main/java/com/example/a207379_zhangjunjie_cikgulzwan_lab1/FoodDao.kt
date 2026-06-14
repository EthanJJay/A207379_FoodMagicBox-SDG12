package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 真正的标准 Room 数据访问对象接口 (FoodDao)
 */
@Dao
interface FoodDao {

    // ==========================================
    // 模块一：收藏美食管理 (Favorite Food Module)
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(food: FoodEntity)

    @Delete
    suspend fun deleteFavorite(food: FoodEntity)

    @Query("SELECT * FROM favorite_foods")
    fun getAllFavorites(): Flow<List<FoodEntity>>


    // ==========================================
    // 模块二：个人资料管理 (User Profile Module)
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserEntity)

    @Query("SELECT * FROM user_profile_table WHERE userId = :id LIMIT 1")
    fun getUserProfileById(id: String): Flow<UserEntity?>
}