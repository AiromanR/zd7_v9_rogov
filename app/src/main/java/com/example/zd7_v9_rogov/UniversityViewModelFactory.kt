package com.example.zd7_v9_rogov

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zd7_v9_rogov.Room.UniversityRepository

class UniversityViewModelFactory(private val repository: UniversityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniversityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniversityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}