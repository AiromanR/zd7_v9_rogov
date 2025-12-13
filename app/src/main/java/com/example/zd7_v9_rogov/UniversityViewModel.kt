package com.example.zd7_v9_rogov

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.zd7_v9_rogov.Room.*
import kotlinx.coroutines.launch

class UniversityViewModel(private val repository: UniversityRepository) : ViewModel() {

    // Основные списки
    val allUniversities = repository.getAllUniversities().asLiveData()
    val allStudents = repository.getAllStudents().asLiveData()
    val allTeachers = repository.getTeachersWithSalaryDetails().asLiveData()

    fun insertUniversity(university: University) = viewModelScope.launch {
        repository.insertUniversity(university)
    }

    fun updateUniversity(university: University) = viewModelScope.launch {
        repository.updateUniversity(university)
    }

    fun deleteUniversity(university: University) = viewModelScope.launch {
        repository.deleteUniversity(university)
    }

    fun getSpecialtiesByUniversity(universityId: Int) = repository.getSpecialtiesByUniversity(universityId).asLiveData()

    fun getAllSpecialties(): LiveData<List<Specialty>> = repository.getAllSpecialties().asLiveData()  // ← ДОБАВЛЕННЫЙ МЕТОД

    fun insertSpecialty(specialty: Specialty) = viewModelScope.launch {
        repository.insertSpecialty(specialty)
    }

    fun insertStudent(student: Student) = viewModelScope.launch {
        repository.insertStudent(student)
    }

    fun insertTeacher(teacher: Teacher) = viewModelScope.launch {
        repository.insertTeacher(teacher)
    }

    fun addStudentToSpecialty(link: StudentSpecialtyLink, onResult: (Result<Long>) -> Unit) = viewModelScope.launch {
        val result = repository.addStudentToSpecialty(link)
        onResult(result)
    }

    fun getStudentLinks(studentId: Int) = repository.getLinksByStudent(studentId).asLiveData()


    fun assignTeacherToSpecialty(link: TeacherSpecialtyLink, onResult: (Result<Long>) -> Unit) = viewModelScope.launch {
        val result = repository.assignTeacherToSpecialty(link)
        onResult(result)
    }

    fun getTeacherLinks(teacherId: Int) = repository.getLinksByTeacher(teacherId).asLiveData()

    private val _apiLoadResult = MutableLiveData<Result<Int>>()
    val apiLoadResult: LiveData<Result<Int>> = _apiLoadResult

    fun loadUniversitiesFromApi() {
        viewModelScope.launch {
            _apiLoadResult.value = repository.loadUniversitiesFromApi()
        }
    }

    suspend fun authenticateTeacher(email: String, password: String): Teacher? {
        val teacher = repository.getTeacherByEmail(email)
        return if (teacher != null && teacher.password == password) teacher else null
    }

    suspend fun authenticateStudent(email: String, password: String): Student? {
        val student = repository.getStudentByEmail(email)
        return if (student != null && student.password == password) student else null
    }
}