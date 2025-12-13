package com.example.zd7_v9_rogov.Room

import androidx.room.Embedded

data class TeacherWithSalary(
    @Embedded val teacher: Teacher,
    val totalHours: Int,
    val effectiveHourlyRate: Double,
    val annualSalary: Double
)