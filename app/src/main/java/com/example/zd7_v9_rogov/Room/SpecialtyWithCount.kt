package com.example.zd7_v9_rogov.Room

import androidx.room.Embedded
import androidx.room.ColumnInfo

data class SpecialtyWithCount(
    @Embedded val specialty: Specialty,

    @ColumnInfo(name = "studentCount")
    val studentCount: Int
)