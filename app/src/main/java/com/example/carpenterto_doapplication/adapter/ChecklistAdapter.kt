package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R

class ChecklistAdapter(private val tasks: List<String>) :
    RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.checkBox.text = tasks[position]
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
