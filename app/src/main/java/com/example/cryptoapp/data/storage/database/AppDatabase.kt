package com.example.cryptoapp.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase

//Database = η βαση που περιεχει τους πινακες (favorites + watchlist)
@Database(
    entities = [
        FavoriteCoinEntity::class,
        WatchlistCoinEntity::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchlistDao(): WatchlistDao
}
