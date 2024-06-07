package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.ReportTaskModel

class ReportTasksAdapter(
    private val reportTaskList: ArrayList<ReportTaskModel>
) : RecyclerView.Adapter<ReportTasksAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_report_tasks, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportTaskList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val reportTaskModel = reportTaskList[position]
        holder.reportTask.text = reportTaskModel.tasksCompleted.joinToString("\n")
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportTask: TextView = itemView.findViewById(R.id.report_task)
    }
}
