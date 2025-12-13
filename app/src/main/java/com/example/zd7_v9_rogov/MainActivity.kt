package com.example.zd7_v9_rogov

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: UniversityViewModel by viewModels {
        UniversityViewModelFactory((application as UniversityApplication).repository)
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        if (sharedPrefs.getString("role", null) != null) {
            startChooseActionActivity()
            return
        }

        val motionLayout = findViewById<MotionLayout>(R.id.motionLayout)

        motionLayout.setTransitionDuration(600)


        findViewById<Button>(R.id.btnMinistryLogin).setOnClickListener {
            val login = findViewById<EditText>(R.id.etMinistryLogin).text.toString().trim()
            val password = findViewById<EditText>(R.id.etMinistryPassword).text.toString().trim()

            if (login == "testtest" && password == "12345678") {
                saveRoleAndProceed("ministry", -1)
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnTeacherLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etTeacherEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etTeacherPassword).text.toString().trim()


            viewModel.viewModelScope.launch {
                val teacher = viewModel.authenticateTeacher(email, password)
                if (teacher != null) {
                    saveRoleAndProceed("teacher", teacher.teacherId)
                } else {
                }
            }
        }

        findViewById<Button>(R.id.btnStudentLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etStudentEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etStudentPassword).text.toString().trim()



            viewModel.viewModelScope.launch {
                val student = viewModel.authenticateStudent(email, password)
                if (student != null) {
                    saveRoleAndProceed("student", student.studentId)
                } else {
                }
            }
        }
    }

    private fun saveRoleAndProceed(role: String, userId: Int) {
        sharedPrefs.edit()
            .putString("role", role)
            .putInt("userId", userId)
            .apply()
        startChooseActionActivity()
    }

    private fun startChooseActionActivity() {
        startActivity(Intent(this, ChooseActionActivity::class.java))
        finish()
    }

}