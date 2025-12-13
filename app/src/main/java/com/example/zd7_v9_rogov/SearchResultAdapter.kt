package com.example.zd7_v9_rogov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v9_rogov.Room.Specialty
import com.example.zd7_v9_rogov.Room.University

data class SearchResult(
    val university: University,
    val matchingSpecialties: List<Specialty>
)

class SearchResultAdapter(private val results: List<SearchResult>) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUniversity: TextView = view.findViewById(android.R.id.text1)
        val tvSpecialties: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.tvUniversity.text = "${result.university.name}\nСайт: ${result.university.webPage}"

        if (result.matchingSpecialties.isEmpty()) {
            holder.tvSpecialties.text = "Специальности не найдены"
        } else {
            val specialtiesText = result.matchingSpecialties.joinToString("\n") { "• ${it.name}" }
            holder.tvSpecialties.text = "Найденные специальности:\n$specialtiesText"
        }
    }

    override fun getItemCount() = results.size
}