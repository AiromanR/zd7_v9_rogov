package com.example.zd7_v9_rogov.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        University::class,
        Specialty::class,
        Student::class,
        Teacher::class,
        StudentSpecialtyLink::class,
        TeacherSpecialtyLink::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UniversityDatabase : RoomDatabase() {

    abstract fun universityDao(): UniversityDao
    abstract fun specialtyDao(): SpecialtyDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun studentSpecialtyLinkDao(): StudentSpecialtyLinkDao
    abstract fun teacherSpecialtyLinkDao(): TeacherSpecialtyLinkDao

    companion object {
        @Volatile
        private var INSTANCE: UniversityDatabase? = null

        fun getDatabase(context: Context): UniversityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UniversityDatabase::class.java,
                    "university_belarus.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}