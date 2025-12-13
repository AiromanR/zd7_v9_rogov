package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "teacher_specialty_links",
    primaryKeys = ["teacherId", "specialtyId"],
    foreignKeys = [
        ForeignKey(
            entity = Teacher::class,
            parentColumns = ["teacherId"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Specialty::class,
            parentColumns = ["specialtyId"],
            childColumns = ["specialtyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("teacherId"), Index("specialtyId")]
)
data class TeacherSpecialtyLink(
    val teacherId: Int,
    val specialtyId: Int,
    val hoursPerYear: Int = 0
)