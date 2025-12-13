package com.example.zd7_v9_rogov

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zd7_v9_rogov.Room.*
import android.widget.AdapterView
import android.widget.ArrayAdapter

class AssignTeacherActivity : AppCompatActivity() {

    private val viewModel: UniversityViewModel by viewModels {
        UniversityViewModelFactory((application as UniversityApplication).repository)
    }

    private lateinit var spinnerUniversity: Spinner
    private lateinit var spinnerSpecialty: Spinner
    private lateinit var spinnerTeacher: Spinner
    private lateinit var etHours: EditText
    private lateinit var btnAssign: Button
    private lateinit var statusText: TextView

    private val universities = mutableListOf<University>()
    private val specialties = mutableListOf<Specialty>()
    private val teachers = mutableListOf<Teacher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_teacher)

        spinnerUniversity = findViewById(R.id.spinnerUniversity)
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty)
        spinnerTeacher = findViewById(R.id.spinnerTeacher)
        etHours = findViewById(R.id.etHours)
        btnAssign = findViewById(R.id.btnAssign)
        statusText = findViewById(R.id.statusText)

        loadUniversities()
        loadTeachers()

        // При выборе университета — загружаем специальности
        spinnerUniversity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == AdapterView.INVALID_POSITION) {
                    spinnerSpecialty.adapter = null
                    return
                }
                val uniId = universities[position].universityId
                loadSpecialties(uniId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerSpecialty.adapter = null
            }
        }

        btnAssign.setOnClickListener {
            assignTeacher()
        }
    }

    private fun loadUniversities() {
        viewModel.allUniversities.observe(this) { list ->
            universities.clear()
            universities.addAll(list)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUniversity.adapter = adapter
        }
    }

    private fun loadSpecialties(universityId: Int) {
        viewModel.getSpecialtiesByUniversity(universityId).observe(this) { list ->
            specialties.clear()
            specialties.addAll(list)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSpecialty.adapter = adapter
        }
    }

    private fun loadTeachers() {
        viewModel.allTeachers.observe(this) { list ->
            teachers.clear()
            teachers.addAll(list.map { it.teacher })
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.teacher.fullName })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTeacher.adapter = adapter
        }
    }

    private fun assignTeacher() {
        val uniPos = spinnerUniversity.selectedItemPosition
        val specPos = spinnerSpecialty.selectedItemPosition
        val teacherPos = spinnerTeacher.selectedItemPosition
        val hoursText = etHours.text.toString()

        if (uniPos == AdapterView.INVALID_POSITION || specPos == AdapterView.INVALID_POSITION ||
            teacherPos == AdapterView.INVALID_POSITION || hoursText.isEmpty()) {
            statusText.text = "Заполните все поля"
            return
        }

        val hours = hoursText.toIntOrNull() ?: run {
            statusText.text = "Некорректное количество часов"
            return
        }

        if (hours <= 0) {
            statusText.text = "Часы должны быть больше 0"
            return
        }

        val teacherId = teachers[teacherPos].teacherId
        val specialtyId = specialties[specPos].specialtyId

        val link = TeacherSpecialtyLink(
            teacherId = teacherId,
            specialtyId = specialtyId,
            hoursPerYear = hours
        )

        viewModel.assignTeacherToSpecialty(link) { result ->
            result.onSuccess {
                runOnUiThread {
                    statusText.text = "Преподаватель назначен на специальность"
                    statusText.setTextColor(getColor(android.R.color.holo_green_dark))
                }
            }
            result.onFailure { e ->
                runOnUiThread {
                    statusText.text = e.message ?: "Ошибка назначения"
                    statusText.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }
    }
}