package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 原生代理数据访问对象 (FoodDao) [cite: 14, 44]
 */
class FoodDao(context: Context) : SQLiteOpenHelper(context, "food_db", null, 1) {

    private val _favoritesFlow = MutableStateFlow<List<FoodEntity>>(emptyList())

    init {
        // 初始化时自动加载历史持久化数据
        refreshFavorites()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建本地闪存表
        db.execSQL("CREATE TABLE favorite_foods (name TEXT PRIMARY KEY, price TEXT, imageRes INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS favorite_foods")
        onCreate(db)
    }

    // 插入美食持久化数据 [cite: 46]
    fun insertFavorite(food: FoodEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", food.name)
            put("price", food.price)
            put("imageRes", food.imageRes)
        }
        db.insertWithOnConflict("favorite_foods", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        refreshFavorites()
    }

    // 删除单条美食持久化数据 [cite: 48]
    fun deleteFavorite(food: FoodEntity) {
        val db = writableDatabase
        db.delete("favorite_foods", "name = ?", arrayOf(food.name))
        refreshFavorites()
    }

    // 获取全部持久化数据流，完全契合实验要求的 Flow<List<Entity>> [cite: 47]
    fun getAllFavorites(): Flow<List<FoodEntity>> {
        return _favoritesFlow
    }

    private fun refreshFavorites() {
        val list = mutableListOf<FoodEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM favorite_foods", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val price = cursor.getString(cursor.getColumnIndexOrThrow("price"))
                val imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes"))
                list.add(FoodEntity(name, price, imageRes))
            } while (cursor.moveToNext())
        }
        cursor.close()
        _favoritesFlow.value = list
    }
}