package com.example.carpenterto_doapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R

class ChecklistAdapter(
    private val context: Context,
    private val tasks: MutableList<String>,
    private val tasksCompleted: MutableList<Boolean>,
    private val onTaskStatusChanged: () -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.checkBox.text = tasks[position]
        holder.checkBox.isChecked = tasksCompleted[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            tasksCompleted[position] = isChecked
            Toast.makeText(context, "${tasks[position]} is ${if (isChecked) "completed" else "not completed"}", Toast.LENGTH_SHORT).show()
            onTaskStatusChanged()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
