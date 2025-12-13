package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val studentId: Int = 0,

    val fullName: String,
    val birthDate: Long,
    val certificateScore: Double,
    val email: String? = null,
    val password: String? = null
)