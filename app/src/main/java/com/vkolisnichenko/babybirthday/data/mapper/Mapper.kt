package com.vkolisnichenko.babybirthday.data.mapper

import com.vkolisnichenko.babybirthday.data.database.entity.BabyInfoEntity
import com.vkolisnichenko.babybirthday.domain.model.BabyInfo

fun BabyInfoEntity.toDomain(): BabyInfo {
    return BabyInfo(
        name = name,
        birthday = birthday,
        photoPath = photoPath
    )
}

fun BabyInfo.toEntity(): BabyInfoEntity {
    return BabyInfoEntity(
        id = 1,
        name = name,
        birthday = birthday,
        photoPath = photoPath
    )
}

fun List<BabyInfoEntity>.toDomainList(): List<BabyInfo> {
    return map { it.toDomain() }
}