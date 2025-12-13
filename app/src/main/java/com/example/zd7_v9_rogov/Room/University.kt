package com.example.zd7_v9_rogov.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universities")
data class University(
    @PrimaryKey(autoGenerate = true)
    val universityId: Int = 0,

    val name: String,
    val webPage: String,
    val region: String? = null
)