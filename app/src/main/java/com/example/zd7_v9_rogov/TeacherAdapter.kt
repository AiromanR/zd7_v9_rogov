package com.example.zd7_v9_rogov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v9_rogov.Room.TeacherWithSalary

class TeacherAdapter(private val teachers: List<TeacherWithSalary>) : RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTeacherName)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvHours: TextView = view.findViewById(R.id.tvHours)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = teachers[position]
        holder.tvName.text = item.teacher.fullName
        holder.tvSalary.text = "Годовая зарплата: ${String.format("%.2f", item.annualSalary)} BYN"
        holder.tvHours.text = "Нагрузка: ${item.totalHours} ч (ставка: ${String.format("%.2f", item.effectiveHourlyRate)}/ч)"
    }

    override fun getItemCount() = teachers.size
}