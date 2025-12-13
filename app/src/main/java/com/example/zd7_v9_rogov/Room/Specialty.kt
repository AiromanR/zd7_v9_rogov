package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "specialties",
    foreignKeys = [
        ForeignKey(
            entity = University::class,
            parentColumns = ["universityId"],
            childColumns = ["universityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("universityId")]
)
data class Specialty(
    @PrimaryKey(autoGenerate = true)
    val specialtyId: Int = 0,

    val name: String,
    val universityId: Int
)