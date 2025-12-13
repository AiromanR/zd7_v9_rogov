package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val teacherId: Int = 0,

    val fullName: String,
    val hourlyRate: Double,
    val overtimeBonusPercent: Double = 0.0,

    val email: String? = null,
    val password: String? = null
)