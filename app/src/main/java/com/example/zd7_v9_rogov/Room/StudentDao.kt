package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert
    suspend fun insert(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students ORDER BY fullName")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE studentId = :id")
    suspend fun getStudentById(id: Int): Student?

    @Query("""
        SELECT COUNT(*) FROM student_specialty_links 
        WHERE specialtyId = :specialtyId AND isBudget = 1
    """)
    suspend fun getBudgetStudentCountForSpecialty(specialtyId: Int): Int

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): Student?
}