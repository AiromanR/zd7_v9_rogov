package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSpecialtyLinkDao {

    @Insert
    suspend fun insert(link: StudentSpecialtyLink): Long

    @Delete
    suspend fun delete(link: StudentSpecialtyLink)

    @Query("SELECT * FROM student_specialty_links WHERE studentId = :studentId")
    fun getLinksByStudent(studentId: Int): Flow<List<StudentSpecialtyLink>>

    @Query("SELECT * FROM student_specialty_links WHERE specialtyId = :specialtyId")
    fun getLinksBySpecialty(specialtyId: Int): Flow<List<StudentSpecialtyLink>>

    @Query("SELECT COUNT(*) > 0 FROM student_specialty_links WHERE studentId = :studentId AND isBudget = 1")
    suspend fun hasBudgetSpecialty(studentId: Int): Boolean
}