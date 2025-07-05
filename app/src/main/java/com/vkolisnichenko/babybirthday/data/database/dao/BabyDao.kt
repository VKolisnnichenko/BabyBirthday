package com.vkolisnichenko.babybirthday.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vkolisnichenko.babybirthday.data.database.entity.BabyInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM baby_info WHERE id = 1")
    fun getBabyInfo(): Flow<List<BabyInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBabyInfo(babyInfo: BabyInfoEntity)

    @Query("DELETE FROM baby_info WHERE id = 1")
    suspend fun deleteBabyInfo()
}