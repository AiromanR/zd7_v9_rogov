package com.example.zd7_v9_rogov

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.zd7_v9_rogov.Room.*

class AssignStudentActivity : AppCompatActivity() {

    private val viewModel: UniversityViewModel by viewModels {
        UniversityViewModelFactory((application as UniversityApplication).repository)
    }

    private lateinit var spinnerUniversity: Spinner
    private lateinit var spinnerSpecialty: Spinner
    private lateinit var spinnerStudent: Spinner
    private lateinit var cbBudget: CheckBox
    private lateinit var btnAssign: Button
    private lateinit var statusText: TextView

    private val universities = mutableListOf<University>()
    private val specialties = mutableListOf<Specialty>()
    private val students = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_student)

        spinnerUniversity = findViewById(R.id.spinnerUniversity)
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty)
        spinnerStudent = findViewById(R.id.spinnerStudent)
        cbBudget = findViewById(R.id.cbBudget)
        btnAssign = findViewById(R.id.btnAssign)
        statusText = findViewById(R.id.statusText)

        loadUniversities()

        spinnerUniversity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val uniId = universities[position].universityId
                loadSpecialties(uniId)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadStudents()

        btnAssign.setOnClickListener {
            assignStudent()
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

    private fun loadStudents() {
        viewModel.allStudents.observe(this) { list ->
            students.clear()
            students.addAll(list)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.fullName })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStudent.adapter = adapter
        }
    }

    private fun assignStudent() {
        val uniPos = spinnerUniversity.selectedItemPosition
        val specPos = spinnerSpecialty.selectedItemPosition
        val studentPos = spinnerStudent.selectedItemPosition

        if (uniPos == AdapterView.INVALID_POSITION || specPos == AdapterView.INVALID_POSITION || studentPos == AdapterView.INVALID_POSITION) {
            statusText.text = "Выберите все поля"
            return
        }

        val studentId = students[studentPos].studentId
        val specialtyId = specialties[specPos].specialtyId
        val isBudget = cbBudget.isChecked

        val link = StudentSpecialtyLink(
            studentId = studentId,
            specialtyId = specialtyId,
            isBudget = isBudget
        )

        viewModel.addStudentToSpecialty(link) { result ->
            result.onSuccess {
                runOnUiThread {
                    statusText.text = "Студент назначен на специальность"
                    statusText.setTextColor(getColor(android.R.color.holo_green_dark))
                }
            }
            result.onFailure { e ->
                runOnUiThread {
                    statusText.text = e.message ?: "Ошибка"
                    statusText.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }
    }
}