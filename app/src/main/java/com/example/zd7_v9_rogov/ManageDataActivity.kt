package com.example.zd7_v9_rogov

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zd7_v9_rogov.Room.*
import java.util.Calendar

class ManageDataActivity : AppCompatActivity() {

    private val viewModel: UniversityViewModel by viewModels {
        UniversityViewModelFactory((application as UniversityApplication).repository)
    }

    private lateinit var formContainer: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var statusText: TextView

    private var selectedType: String = ""

    // Для университетов
    private var spinnerUniversity: Spinner? = null
    private var etWebPage: EditText? = null
    private var etRegion: EditText? = null
    private val universities = mutableListOf<University>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_data)

        selectedType = intent.getStringExtra("type") ?: ""

        formContainer = findViewById(R.id.formContainer)
        btnSave = findViewById(R.id.btnSave)
        statusText = findViewById(R.id.statusText)

        setupFormByType()
    }

    private fun setupFormByType() {
        formContainer.removeAllViews()

        when (selectedType) {
            "universities" -> setupUniversityEditForm()
            "specialties" -> setupSpecialtyForm()
            "students" -> setupStudentForm()
            "teachers" -> setupTeacherForm()
            else -> statusText.text = "Неизвестный тип"
        }
    }

    // НОВАЯ ФОРМА: Редактирование существующего университета
    private fun setupUniversityEditForm() {
        val tvTitle = TextView(this).apply {
            text = "Выберите университет для редактирования"
            textSize = 18f
            gravity = View.TEXT_ALIGNMENT_CENTER
        }
        formContainer.addView(tvTitle)

        spinnerUniversity = Spinner(this)
        formContainer.addView(spinnerUniversity!!)

        etWebPage = EditText(this).apply { hint = "Сайт" }
        etRegion = EditText(this).apply { hint = "Область" }

        formContainer.addView(etWebPage!!)
        formContainer.addView(etRegion!!)

        // Загружаем список университетов
        viewModel.allUniversities.observe(this) { list ->
            universities.clear()
            universities.addAll(list)

            if (list.isEmpty()) {
                statusText.text = "Нет университетов в базе"
                btnSave.isEnabled = false
                return@observe
            }

            val names = list.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUniversity!!.adapter = adapter

            btnSave.isEnabled = true
        }

        // При выборе университета — заполняем поля
        spinnerUniversity!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUni = universities[position]
                etWebPage!!.setText(selectedUni.webPage)
                etRegion!!.setText(selectedUni.region ?: "")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Сохранение изменений
        btnSave.text = "Сохранить изменения"
        btnSave.setOnClickListener {
            val position = spinnerUniversity!!.selectedItemPosition
            if (position == AdapterView.INVALID_POSITION) {
                statusText.text = "Выберите университет"
                return@setOnClickListener
            }

            val selectedUni = universities[position].copy(
                webPage = etWebPage!!.text.toString(),
                region = etRegion!!.text.toString()
            )

            viewModel.updateUniversity(selectedUni)
            statusText.text = "Университет обновлён"
        }
    }

    private fun setupSpecialtyForm() {
        val etName = EditText(this).apply { hint = "Название специальности" }
        formContainer.addView(TextView(this).apply { text = "Выберите университет:" })

        // Создаём спиннер один раз
        spinnerUniversity = Spinner(this)
        formContainer.addView(spinnerUniversity!!)

        formContainer.addView(etName)

        // Загружаем университеты
        viewModel.allUniversities.observe(this) { list ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUniversity!!.adapter = adapter
        }

        btnSave.setOnClickListener {
            val pos = spinnerUniversity!!.selectedItemPosition
            if (pos == AdapterView.INVALID_POSITION) {
                statusText.text = "Выберите университет"
                return@setOnClickListener
            }

            val universityId = viewModel.allUniversities.value?.get(pos)?.universityId ?: return@setOnClickListener

            val specialty = Specialty(
                name = etName.text.toString(),
                universityId = universityId
            )
            viewModel.insertSpecialty(specialty)
            statusText.text = "Специальность добавлена"
        }
    }

    private fun setupStudentForm() {
        val etFullName = EditText(this).apply { hint = "ФИО" }
        val tvBirthDate = TextView(this).apply {
            hint = "Дата рождения (нажмите для выбора)"
            setTextColor(getColor(android.R.color.black))
            setHintTextColor(getColor(android.R.color.darker_gray))
            setPadding(32, 32, 32, 32)
            background = getDrawable(android.R.drawable.edit_text)
        }
        val etScore = EditText(this).apply { hint = "Балл аттестата" }
        val etEmail = EditText(this).apply { hint = "Email" }
        val etPassword = EditText(this).apply { hint = "Пароль" }

        formContainer.addView(etFullName)
        formContainer.addView(tvBirthDate)
        formContainer.addView(etScore)
        formContainer.addView(etEmail)
        formContainer.addView(etPassword)

        // Текущая дата
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Ограничения
        val maxDate = calendar.timeInMillis  // Сегодня (не младше 10 лет — но DatePicker сам ограничит)
        calendar.add(Calendar.YEAR, -10)
        val minDateFor10Years = calendar.timeInMillis  // 10 лет назад
        calendar.set(year - 100, month, day)
        val minDate = calendar.timeInMillis  // 100 лет назад

        var selectedBirthDate = 0L

        // DatePicker
        tvBirthDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    selectedBirthDate = calendar.timeInMillis
                    tvBirthDate.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                },
                year - 18, month, day  // По умолчанию 18 лет назад
            )

            datePicker.datePicker.maxDate = minDateFor10Years  // Не младше 10 лет
            datePicker.datePicker.minDate = minDate  // Не старше 100 лет
            datePicker.show()
        }

        btnSave.setOnClickListener {
            try {
                if (selectedBirthDate == 0L) {
                    statusText.text = "Выберите дату рождения"
                    return@setOnClickListener
                }

                val score = etScore.text.toString().toDouble()

                val student = Student(
                    fullName = etFullName.text.toString(),
                    birthDate = selectedBirthDate,
                    certificateScore = score,
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString()
                )
                viewModel.insertStudent(student)
                statusText.text = "Студент добавлен"
            } catch (e: Exception) {
                statusText.text = "Ошибка ввода данных"
            }
        }
    }

    private fun setupTeacherForm() {
        val etFullName = EditText(this).apply { hint = "ФИО" }
        val etHourlyRate = EditText(this).apply { hint = "Почасовая ставка" }
        val etBonusPercent = EditText(this).apply { hint = "Премия за переработку (%)" }
        val etEmail = EditText(this).apply { hint = "Email" }
        val etPassword = EditText(this).apply { hint = "Пароль" }

        formContainer.addView(etFullName)
        formContainer.addView(etHourlyRate)
        formContainer.addView(etBonusPercent)
        formContainer.addView(etEmail)
        formContainer.addView(etPassword)

        btnSave.setOnClickListener {
            try {
                val hourlyRate = etHourlyRate.text.toString().toDouble()
                val bonus = etBonusPercent.text.toString().toDouble()

                val teacher = Teacher(
                    fullName = etFullName.text.toString(),
                    hourlyRate = hourlyRate,
                    overtimeBonusPercent = bonus,
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString()
                )
                viewModel.insertTeacher(teacher)
                statusText.text = "Преподаватель добавлен"
            } catch (e: Exception) {
                statusText.text = "Ошибка ввода данных"
            }
        }
    }
}