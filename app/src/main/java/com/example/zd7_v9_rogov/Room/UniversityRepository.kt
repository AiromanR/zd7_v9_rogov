package com.example.zd7_v9_rogov.Room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class UniversityRepository(private val database: UniversityDatabase) {

    fun getAllUniversities(): Flow<List<University>> = database.universityDao().getAllUniversities()

    fun getAllSpecialties(): Flow<List<Specialty>> = database.specialtyDao().getAllSpecialties()
    suspend fun getTeacherByEmail(email: String): Teacher? = database.teacherDao().getTeacherByEmail(email)

    suspend fun getStudentByEmail(email: String): Student? = database.studentDao().getStudentByEmail(email)

    suspend fun insertUniversity(university: University): Long = database.universityDao().insert(university)

    suspend fun updateUniversity(university: University) = database.universityDao().update(university)

    suspend fun deleteUniversity(university: University) = database.universityDao().delete(university)

    suspend fun getUniversityById(id: Int): University? = database.universityDao().getUniversityById(id)

    fun searchUniversitiesByName(query: String): Flow<List<University>> = database.universityDao().searchUniversitiesByName(query)


    suspend fun insertSpecialty(specialty: Specialty): Long = database.specialtyDao().insert(specialty)

    suspend fun updateSpecialty(specialty: Specialty) = database.specialtyDao().update(specialty)

    suspend fun deleteSpecialty(specialty: Specialty) = database.specialtyDao().delete(specialty)

    fun getSpecialtiesByUniversity(universityId: Int): Flow<List<Specialty>> = database.specialtyDao().getSpecialtiesByUniversity(universityId)


    suspend fun insertStudent(student: Student): Long = database.studentDao().insert(student)

    suspend fun updateStudent(student: Student) = database.studentDao().update(student)

    suspend fun deleteStudent(student: Student) = database.studentDao().delete(student)

    fun getAllStudents(): Flow<List<Student>> = database.studentDao().getAllStudents()


    suspend fun insertTeacher(teacher: Teacher): Long = database.teacherDao().insert(teacher)

    suspend fun updateTeacher(teacher: Teacher) = database.teacherDao().update(teacher)

    suspend fun deleteTeacher(teacher: Teacher) = database.teacherDao().delete(teacher)

    fun getTeachersWithSalaryDetails(): Flow<List<TeacherWithSalary>> = database.teacherDao().getTeachersWithSalaryDetails()


    suspend fun addStudentToSpecialty(link: StudentSpecialtyLink): Result<Long> {
        if (link.isBudget) {
            val hasBudget = database.studentSpecialtyLinkDao().hasBudgetSpecialty(link.studentId)
            if (hasBudget) {
                return Result.failure(Exception("У студента уже есть бюджетная специальность"))
            }

            val currentBudgetCount = database.studentDao().getBudgetStudentCountForSpecialty(link.specialtyId)
            if (currentBudgetCount >= 50) {
                return Result.failure(Exception("Лимит 50 бюджетных мест на специальности превышен"))
            }
        }

        return try {
            val id = database.studentSpecialtyLinkDao().insert(link)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeStudentFromSpecialty(link: StudentSpecialtyLink) {
        database.studentSpecialtyLinkDao().delete(link)
    }

    fun getLinksByStudent(studentId: Int): Flow<List<StudentSpecialtyLink>> = database.studentSpecialtyLinkDao().getLinksByStudent(studentId)

    suspend fun assignTeacherToSpecialty(link: TeacherSpecialtyLink): Result<Long> {
        val currentHours = database.teacherSpecialtyLinkDao().getTotalHoursForTeacher(link.teacherId)
        val newTotal = currentHours + link.hoursPerYear

        if (newTotal > 2000) {
            return Result.failure(Exception("Превышен максимальный лимит нагрузки (2000 часов)"))
        }

        return try {
            val id = database.teacherSpecialtyLinkDao().insert(link)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeTeacherFromSpecialty(link: TeacherSpecialtyLink) {
        database.teacherSpecialtyLinkDao().delete(link)
    }

    fun getLinksByTeacher(teacherId: Int): Flow<List<TeacherSpecialtyLink>> = database.teacherSpecialtyLinkDao().getLinksByTeacher(teacherId)

    suspend fun loadUniversitiesFromApi(): Result<Int> {
        return try {
            val response = RetrofitClient.apiService.getUniversitiesByCountry()
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка сети: ${response.code()}"))
            }

            val apiList = response.body() ?: return Result.failure(Exception("Пустой ответ"))


            val existingUniversities = database.universityDao().getAllUniversities().first()

            var savedCount = 0
            for (apiUni in apiList) {
                val webPage = if (apiUni.web_pages.isNotEmpty()) apiUni.web_pages[0] else ""

                val exists = existingUniversities.any { uni ->
                    uni.name.equals(apiUni.name, ignoreCase = true) || uni.webPage == webPage
                }

                if (!exists) {
                    val university = University(
                        name = apiUni.name,
                        webPage = webPage,
                        region = apiUni.state_province
                    )
                    database.universityDao().insert(university)
                    savedCount++
                }
            }

            Result.success(savedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}