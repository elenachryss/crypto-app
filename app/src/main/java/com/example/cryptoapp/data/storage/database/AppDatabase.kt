package com.example.cryptoapp.data.storage.database


import androidx.room.Database
import androidx.room.RoomDatabase

//Database = η βαση που περιεχει τους πινακες με τα favorite coins
@Database(entities = [FavoriteCoinEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}