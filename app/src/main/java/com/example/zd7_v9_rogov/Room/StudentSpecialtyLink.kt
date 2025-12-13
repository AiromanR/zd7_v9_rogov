package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "student_specialty_links",
    primaryKeys = ["studentId", "specialtyId"],
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Specialty::class,
            parentColumns = ["specialtyId"],
            childColumns = ["specialtyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("studentId"),
        Index("specialtyId"),
        Index(value = ["studentId", "isBudget"])
    ]
)
data class StudentSpecialtyLink(
    val studentId: Int,
    val specialtyId: Int,
    val isBudget: Boolean = false
)