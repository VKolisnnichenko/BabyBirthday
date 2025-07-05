package com.vkolisnichenko.babybirthday.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vkolisnichenko.babybirthday.data.database.converters.Converters
import com.vkolisnichenko.babybirthday.data.database.dao.BabyDao
import com.vkolisnichenko.babybirthday.data.database.entity.BabyInfoEntity

@Database(
    entities = [BabyInfoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao

    companion object {
        const val DATABASE_NAME = "baby_database"
    }
}