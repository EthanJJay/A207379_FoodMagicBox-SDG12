package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 标准 Room 数据库持有单例类 (AppDatabase)
 */
@Database(entities = [FoodEntity::class, UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 严格落实单例模式初始化实例 (Singleton Pattern)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_app_room_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}