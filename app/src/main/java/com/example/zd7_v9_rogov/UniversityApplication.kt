package com.example.zd7_v9_rogov

import android.app.Application
import com.example.zd7_v9_rogov.Room.UniversityDatabase
import com.example.zd7_v9_rogov.Room.UniversityRepository

class UniversityApplication : Application() {

    val database by lazy { UniversityDatabase.getDatabase(this) }
    val repository by lazy { UniversityRepository(database) }
}