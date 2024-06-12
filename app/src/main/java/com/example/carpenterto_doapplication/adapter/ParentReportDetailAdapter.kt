package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.TasksCompletedModel

class ParentReportDetailAdapter(
    private val tasksCompletedList: List<TasksCompletedModel>
) : RecyclerView.Adapter<ParentReportDetailAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_parent_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasksCompletedList.size
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val task = tasksCompletedList[position]
        holder.maintenanceType.text = task.maintenanceType
        holder.arrowImage.setImageResource(task.arrowImage)

        val childAdapter = ReportDetailAdapter(task.tasksCompleted)
        holder.childRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(holder.itemView.context)
            adapter = childAdapter
        }
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val maintenanceType: TextView = itemView.findViewById(R.id.maintenance_type)
        val arrowImage: ImageView = itemView.findViewById(R.id.left_arrow)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.tasks_list)
    }
}
