package com.vkolisnichenko.babybirthday.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "baby_info")
data class BabyInfoEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val birthday: LocalDate,
    val photoPath: String?
)
