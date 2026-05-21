package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.content.Context

/**
 * 数据库单例持有类 (AppDatabase) [cite: 49, 50]
 */
class AppDatabase private constructor(context: Context) {

    private val foodDao = FoodDao(context)

    // 包含你的 DAO [cite: 51]
    fun foodDao(): FoodDao = foodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 严格落实单例模式 (Singleton instance)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}