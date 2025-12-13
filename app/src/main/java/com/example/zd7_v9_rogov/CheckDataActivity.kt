package com.example.zd7_v9_rogov

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v9_rogov.Room.*

class CheckDataActivity : AppCompatActivity() {

    private lateinit var viewModel: UniversityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_data)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnRefresh = findViewById(R.id.btnRefresh)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val repository = (application as UniversityApplication).repository
        viewModel = ViewModelProvider(this, UniversityViewModelFactory(repository))[UniversityViewModel::class.java]

        val type = intent.getStringExtra("type") ?: "universities"


        btnRefresh.visibility = View.VISIBLE
        btnRefresh.setOnClickListener {
            loadData(type)
        }

        // При первом открытии сразу подгружаем данные
        loadData(type)
    }
    private fun setupUniversities() {
        viewModel.allUniversities.observe(this) { universities ->
            progressBar.visibility = View.GONE
            if (universities.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Нет университетов"
            } else {
                recyclerView.adapter = UniversityAdapter(universities)
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
    private fun loadData(type: String) {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.GONE

        when (type) {
            "universities" -> {
                viewModel.allUniversities.observe(this) { universities ->
                    progressBar.visibility = View.GONE
                    if (universities.isEmpty()) {
                        tvEmpty.text = "Нет университетов. Нажмите \"Обновить\""
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = UniversityAdapter(universities)
                        recyclerView.visibility = View.VISIBLE
                    }
                }

                if (viewModel.allUniversities.value?.isEmpty() == true) {
                    viewModel.loadUniversitiesFromApi()
                }
            }

            "students" -> {
                viewModel.allStudents.observe(this) { students ->
                    progressBar.visibility = View.GONE
                    if (students.isEmpty()) {
                        tvEmpty.text = "Нет студентов"
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = StudentAdapter(students)
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }

            "teachers" -> {
                viewModel.allTeachers.observe(this) { teachers ->
                    progressBar.visibility = View.GONE
                    if (teachers.isEmpty()) {
                        tvEmpty.text = "Нет преподавателей"
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = TeacherAdapter(teachers)
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }

            "specialties" -> {
                viewModel.getAllSpecialties().observe(this) { specialties ->
                    progressBar.visibility = View.GONE
                    if (specialties.isEmpty()) {
                        tvEmpty.text = "Нет специальностей"
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = SpecialtyAdapter(specialties)
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }

            else -> {
                progressBar.visibility = View.GONE
                tvEmpty.text = "Неизвестный тип данных"
                tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}