package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UniversityDao {

    @Insert
    suspend fun insert(university: University): Long

    @Update
    suspend fun update(university: University)

    @Delete
    suspend fun delete(university: University)

    @Query("SELECT * FROM universities ORDER BY universityId")
    fun getAllUniversities(): Flow<List<University>>

    @Query("SELECT * FROM universities WHERE universityId = :id")
    suspend fun getUniversityById(id: Int): University?

    @Query("SELECT * FROM universities WHERE region = :region ORDER BY name")
    fun getUniversitiesByRegion(region: String): Flow<List<University>>

    @Query("SELECT * FROM universities WHERE name LIKE '%' || :query || '%' ")
    fun searchUniversitiesByName(query: String): Flow<List<University>>
}