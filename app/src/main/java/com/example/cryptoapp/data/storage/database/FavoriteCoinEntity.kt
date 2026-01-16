package com.example.cryptoapp.data.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity = πινακας στη Room
//θα αποθηκευουμε μονο το symbol για να ξερουμε αν ειναι favorite
@Entity(tableName = "favorites")
data class FavoriteCoinEntity (
    @PrimaryKey val symbol: String,
)