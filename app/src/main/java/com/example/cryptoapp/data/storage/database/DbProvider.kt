package com.example.cryptoapp.data.storage.database

import android.content.Context
import androidx.room.Room

//singleton για να εχουμε 1 instance της βασης σε ολο το app
object DbProvider {
    private var db: AppDatabase? = null

    fun getDb(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "crypto_db"
            ).build()
        }
        return db!!
    }
}