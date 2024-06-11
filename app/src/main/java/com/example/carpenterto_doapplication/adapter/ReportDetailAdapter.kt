package com.example.carpenterto_doapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R

class ReportDetailAdapter(
    private val tasksList: List<String>
) : RecyclerView.Adapter<ReportDetailAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val task = tasksList[position]
        holder.taskCheckBox.text = task
        holder.taskCheckBox.isChecked = true
        holder.taskCheckBox.isClickable = false
        Log.d("TAG", "onBindViewHolder: $task")
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
