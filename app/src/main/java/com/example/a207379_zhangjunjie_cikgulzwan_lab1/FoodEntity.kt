package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 收藏美食本地数据库表实体类 (FoodEntity)
 */
@Entity(tableName = "favorite_foods")
data class FoodEntity(
    @PrimaryKey val name: String, // 以美食名字作为主键
    val price: String,
    val imageRes: Int
)

/**
 * 个人资料本地数据库表实体类 (UserEntity)
 */
@Entity(tableName = "user_profile_table")
data class UserEntity(
    @PrimaryKey val userId: String = "A207379", // 以用户学号作为主键
    val username: String = "ZHANGJUNJIE",
    val age: Int = 20,
    val gender: String = "Male",
    val residence: String = "Kajang, Malaysia"
)