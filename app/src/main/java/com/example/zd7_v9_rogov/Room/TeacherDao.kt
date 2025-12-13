package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Insert
    suspend fun insert(teacher: Teacher): Long

    @Update
    suspend fun update(teacher: Teacher)

    @Delete
    suspend fun delete(teacher: Teacher)

    @Query("SELECT * FROM teachers ORDER BY fullName")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE teacherId = :id")
    suspend fun getTeacherById(id: Int): Teacher?


    @Query("""
    SELECT t.*,
           COALESCE(SUM(tsl.hoursPerYear), 0) as totalHours,
           CASE 
               WHEN COALESCE(SUM(tsl.hoursPerYear), 0) > 1440
               THEN t.hourlyRate * (1 + t.overtimeBonusPercent / 100.0)
               ELSE t.hourlyRate
           END as effectiveHourlyRate,
           CASE 
               WHEN COALESCE(SUM(tsl.hoursPerYear), 0) > 1440
               THEN (COALESCE(SUM(tsl.hoursPerYear), 0) * t.hourlyRate * (1 + t.overtimeBonusPercent / 100.0))
               ELSE (COALESCE(SUM(tsl.hoursPerYear), 0) * t.hourlyRate)
           END as annualSalary
    FROM teachers t
    LEFT JOIN teacher_specialty_links tsl ON t.teacherId = tsl.teacherId
    GROUP BY t.teacherId
    ORDER BY t.fullName
""")
    fun getTeachersWithSalaryDetails(): Flow<List<TeacherWithSalary>>

    @Query("SELECT * FROM teachers WHERE email = :email LIMIT 1")
    suspend fun getTeacherByEmail(email: String): Teacher?
}