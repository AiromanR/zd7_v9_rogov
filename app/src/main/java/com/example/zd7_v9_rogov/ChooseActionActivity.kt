package com.example.zd7_v9_rogov

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ChooseActionActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private var role: String? = null
    private var userId: Int = -1
    private lateinit var etSearch: EditText
    private lateinit var viewModel: UniversityViewModel
    private lateinit var recyclerSearchResults: RecyclerView
    private lateinit var tvNoResults: TextView
    private fun showTeacherSpecialtiesDialog(teacherId: Int) {
        val repository = (application as UniversityApplication).repository

        repository.getLinksByTeacher(teacherId).asLiveData().observe(this) { links ->
            if (links.isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Мои специальности")
                builder.setMessage("У вас нет назначенных специальностей")
                builder.setPositiveButton("OK", null)
                builder.show()
                return@observe
            }

            val specialtyIds = links.map { it.specialtyId }
            repository.getAllSpecialties().asLiveData().observe(this) { allSpecialties ->
                val sb = StringBuilder("Ваши специальности:\n\n")
                for (link in links) {
                    val specialtyName = allSpecialties.find { it.specialtyId == link.specialtyId }?.name ?: "Неизвестно"
                    sb.append("• $specialtyName\n")
                    sb.append("  Часы в год: ${link.hoursPerYear}\n\n")
                }

                val scrollView = ScrollView(this)
                val textView = TextView(this).apply {
                    text = sb.toString().trim()
                    textSize = 16f
                    setPadding(48, 32, 48, 32)
                }
                scrollView.addView(textView)

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Мои специальности")
                builder.setView(scrollView)
                builder.setPositiveButton("OK", null)
                builder.show()
            }
        }
    }

    private fun showTeacherSalaryDialog(teacherId: Int) {
        val repository = (application as UniversityApplication).repository

        repository.getTeachersWithSalaryDetails().asLiveData().observe(this) { list ->
            val teacherData = list.find { it.teacher.teacherId == teacherId }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Моя зарплата")

            if (teacherData == null) {
                builder.setMessage("Данные о зарплате не найдены")
                builder.setPositiveButton("OK", null)
                builder.show()
                return@observe
            }

            repository.getLinksByTeacher(teacherId).asLiveData().observe(this) { links ->
                val sb = StringBuilder()
                if (links.isNotEmpty()) {
                    sb.append("Назначенные специальности:\n")
                    repository.getAllSpecialties().asLiveData().observe(this) { allSpecialties ->
                        for (link in links) {
                            val name = allSpecialties.find { it.specialtyId == link.specialtyId }?.name ?: "Неизвестно"
                            sb.append("• $name (${link.hoursPerYear} ч)\n")
                        }
                        sb.append("\n")
                    }
                }

                sb.append("""
                Базовая ставка: ${teacherData.teacher.hourlyRate} ₽/час
                
                Общая нагрузка: ${teacherData.totalHours} часов/год
                
                Премия за переработку: ${teacherData.teacher.overtimeBonusPercent}%
                
                Годовая зарплата: ${String.format("%.2f", teacherData.annualSalary)} ₽
            """.trimIndent())

                val scrollView = ScrollView(this)
                val textView = TextView(this).apply {
                    text = sb.toString()
                    textSize = 18f
                    setPadding(48, 48, 48, 48)
                }
                scrollView.addView(textView)

                builder.setView(scrollView)
                builder.setPositiveButton("OK", null)
                builder.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_action)

        val repository = (application as UniversityApplication).repository
        viewModel = ViewModelProvider(this, UniversityViewModelFactory(repository))[UniversityViewModel::class.java]

        etSearch = findViewById(R.id.etSearch)
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults)
        tvNoResults = findViewById(R.id.tvNoResults)

        recyclerSearchResults.layoutManager = LinearLayoutManager(this)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                if (query.isEmpty()) {
                    recyclerSearchResults.visibility = View.GONE
                    tvNoResults.visibility = View.VISIBLE
                    tvNoResults.text = "Введите запрос для поиска"
                    return
                }

                performSearch(query)
            }
        })

        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        role = sharedPrefs.getString("role", null)
        userId = sharedPrefs.getInt("userId", -1)

        if (role == null) {
            Toast.makeText(this, "Авторизация не пройдена", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setupButtonsByRole()
    }

    private fun performSearch(query: String) {
        val repository = (application as UniversityApplication).repository

        viewModel.allUniversities.observe(this) { universities ->
            viewModel.getAllSpecialties().observe(this) { allSpecialties ->
                val results = mutableListOf<SearchResult>()

                for (university in universities) {
                    if (university.name.lowercase().contains(query)) {
                        results.add(SearchResult(university, allSpecialties.filter { it.universityId == university.universityId }))
                        continue
                    }

                    val matchingSpecs = allSpecialties.filter {
                        it.universityId == university.universityId && it.name.lowercase().contains(query)
                    }

                    if (matchingSpecs.isNotEmpty()) {
                        results.add(SearchResult(university, matchingSpecs))
                    }
                }

                if (results.isEmpty()) {
                    recyclerSearchResults.visibility = View.GONE
                    tvNoResults.visibility = View.VISIBLE
                    tvNoResults.text = "Ничего не найдено"
                } else {
                    recyclerSearchResults.visibility = View.VISIBLE
                    tvNoResults.visibility = View.GONE
                    recyclerSearchResults.adapter = SearchResultAdapter(results)
                }
            }
        }
    }
    private fun setupButtonsByRole() {
        findViewById<Button>(R.id.btnCheckUniversities).setOnClickListener {
            startCheckActivity("universities")
        }

        if (role == "ministry") {
            findViewById<Button>(R.id.btnManageUniversities).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnManageStudents).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnManageTeachers).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnManageSpecialties).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnAssignStudents).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnAssignTeachers).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnAssignStudents).setOnClickListener {
                startActivity(Intent(this, AssignStudentActivity::class.java))
            }

            findViewById<Button>(R.id.btnAssignTeachers).setOnClickListener {
                startActivity(Intent(this, AssignTeacherActivity::class.java))
            }
            findViewById<Button>(R.id.btnManageUniversities).setOnClickListener {
                startManageActivity("universities")
            }
            findViewById<Button>(R.id.btnManageStudents).setOnClickListener {
                startManageActivity("students")
            }
            findViewById<Button>(R.id.btnManageTeachers).setOnClickListener {
                startManageActivity("teachers")
            }
            findViewById<Button>(R.id.btnManageSpecialties).setOnClickListener {
                startManageActivity("specialties")
            }
        }

        if (role == "teacher") {
            findViewById<Button>(R.id.btnMySpecialties).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnMySalary).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnMySpecialties).setOnClickListener {
                showTeacherSpecialtiesDialog(userId)
            }

            findViewById<Button>(R.id.btnMySalary).setOnClickListener {
                showTeacherSalaryDialog(userId)
            }
        }

        if (role == "student") {
            findViewById<Button>(R.id.btnMySpecialties).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnMySpecialties).setOnClickListener {
                showStudentSpecialtiesDialog(userId)
            }
        }
    }

    private fun showStudentSpecialtiesDialog(studentId: Int) {
        val repository = (application as UniversityApplication).repository

        repository.getLinksByStudent(studentId).asLiveData().observe(this) { links ->
            if (links.isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Мои специальности")
                builder.setMessage("Вы не зачислены ни на одну специальность")
                builder.setPositiveButton("OK", null)
                builder.show()
                return@observe
            }

            repository.getAllSpecialties().asLiveData().observe(this) { allSpecialties ->
                val sb = StringBuilder("Ваши специальности:\n\n")
                for (link in links) {
                    val specialtyName = allSpecialties.find { it.specialtyId == link.specialtyId }?.name ?: "Неизвестная специальность"
                    val budgetStatus = if (link.isBudget) " (Бюджет)" else " (Платно)"
                    sb.append("• $specialtyName$budgetStatus\n\n")
                }

                val scrollView = ScrollView(this)
                val textView = TextView(this).apply {
                    text = sb.toString().trim()
                    textSize = 16f
                    setPadding(48, 32, 48, 32)
                }
                scrollView.addView(textView)

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Мои специальности")
                builder.setView(scrollView)
                builder.setPositiveButton("OK", null)
                builder.show()
            }
        }
    }

    private fun startCheckActivity(dataType: String) {
        val intent = Intent(this, CheckDataActivity::class.java)
        intent.putExtra("type", dataType)
        startActivity(intent)
    }

    private fun startManageActivity(dataType: String) {
        val intent = Intent(this, ManageDataActivity::class.java)
        intent.putExtra("type", dataType)
        startActivity(intent)
    }

    fun logout(view: View) {
        sharedPrefs.edit().clear().apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}