package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialtyDao {

    @Insert
    suspend fun insert(specialty: Specialty): Long

    @Update
    suspend fun update(specialty: Specialty)

    @Delete
    suspend fun delete(specialty: Specialty)

    @Query("SELECT * FROM specialties WHERE universityId = :universityId ORDER BY name")
    fun getSpecialtiesByUniversity(universityId: Int): Flow<List<Specialty>>

    @Query("SELECT * FROM specialties WHERE specialtyId = :id")
    suspend fun getSpecialtyById(id: Int): Specialty?

    @Query("SELECT * FROM specialties ORDER BY name")
    fun getAllSpecialties(): Flow<List<Specialty>>

    @Query("""
        SELECT s.*, 
               COUNT(ssl.studentId) as studentCount
        FROM specialties s
        LEFT JOIN student_specialty_links ssl ON s.specialtyId = ssl.specialtyId
        GROUP BY s.specialtyId
    """)
    fun getSpecialtiesWithStudentCount(): Flow<List<SpecialtyWithCount>>
}