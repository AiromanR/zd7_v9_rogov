package com.example.zd7_v9_rogov.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherSpecialtyLinkDao {

    @Insert
    suspend fun insert(link: TeacherSpecialtyLink): Long

    @Delete
    suspend fun delete(link: TeacherSpecialtyLink)

    @Query("SELECT * FROM teacher_specialty_links WHERE teacherId = :teacherId")
    fun getLinksByTeacher(teacherId: Int): Flow<List<TeacherSpecialtyLink>>

    @Query("SELECT * FROM teacher_specialty_links WHERE specialtyId = :specialtyId")
    fun getLinksBySpecialty(specialtyId: Int): Flow<List<TeacherSpecialtyLink>>


    @Query("SELECT COALESCE(SUM(hoursPerYear), 0) FROM teacher_specialty_links WHERE teacherId = :teacherId")
    suspend fun getTotalHoursForTeacher(teacherId: Int): Int
}