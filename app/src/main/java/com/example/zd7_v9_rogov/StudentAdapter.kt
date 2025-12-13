package com.example.zd7_v9_rogov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v9_rogov.Room.Student
import java.text.SimpleDateFormat
import java.util.*

class StudentAdapter(private val students: List<Student>) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvBirthDate: TextView = view.findViewById(R.id.tvBirthDate)
    }

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text = student.fullName
        holder.tvScore.text = "Балл: ${student.certificateScore}"
        holder.tvBirthDate.text = "Дата рождения: ${dateFormat.format(Date(student.birthDate))}"
    }

    override fun getItemCount() = students.size
}