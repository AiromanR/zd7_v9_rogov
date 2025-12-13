package com.example.zd7_v9_rogov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v9_rogov.Room.University
import com.squareup.picasso.Picasso

class UniversityAdapter(private val universities: List<University>) : RecyclerView.Adapter<UniversityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        val tvName: TextView = view.findViewById(R.id.tvUniversityName)
        val tvWebPage: TextView = view.findViewById(R.id.tvWebPage)
        val tvRegion: TextView = view.findViewById(R.id.tvRegion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_university, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uni = universities[position]

        Picasso.get()
            .load(R.drawable.mini)
            .into(holder.ivLogo)

        holder.tvName.text = uni.name
        holder.tvWebPage.text = uni.webPage
        holder.tvRegion.text = uni.region ?: "Область не указана"
    }

    override fun getItemCount() = universities.size
}