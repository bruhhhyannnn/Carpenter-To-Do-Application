package com.example.carpenterto_doapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.MaintenanceTypesModel
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.firestore.FirebaseFirestore

class ParentChecklistAdapter(
    private val parentItemList: List<MaintenanceTypesModel>,
    private val userId: String,
    private val machineName: String,
    private val getMachineTaskDataFromFirebase: (String, Int, (TaskModel) -> Unit) -> Unit
) : RecyclerView.Adapter<ParentChecklistAdapter.ParentRecyclerViewHolder>() {

    private val collectionNameMap = mapOf(
        "Daily Maintenance" to "dailyMaintenance",
        "Monthly Maintenance" to "monthlyMaintenance",
        "As Needed Maintenance" to "asNeededMaintenance",
        "Suggested Maintenance" to "suggestedMaintenance"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_parent_checklist, parent, false)
        return ParentRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parentItemList.size
    }

    override fun onBindViewHolder(holder: ParentRecyclerViewHolder, position: Int) {
        val parentItem = parentItemList[position]

        holder.maintenanceType.text = parentItem.maintenanceType
        holder.arrowImage.setImageResource(parentItem.arrowImage)

        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        val isExpanded = parentItem.isExpanded
        holder.childRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.arrowImage.rotation = if (isExpanded) -90f else 0f

        holder.itemView.setOnClickListener {
            holder.progressBar.visibility = View.VISIBLE
            holder.childRecyclerView.visibility = View.GONE

            val expanded = parentItem.isExpanded
            parentItemList.forEachIndexed { index, item ->
                if (item.isExpanded) {
                    item.isExpanded = false
                    notifyItemChanged(index)
                }
            }
            parentItem.isExpanded = !expanded

            if (parentItem.isExpanded) {
                val collectionName = collectionNameMap[parentItem.maintenanceType] ?: parentItem.maintenanceType
                getMachineTaskDataFromFirebase(collectionName, position) { taskModel ->
                    parentItem.childItemList.clear()
                    parentItem.childItemList.add(taskModel)
                    holder.childRecyclerView.adapter = ChecklistAdapter(parentItem.childItemList, userId, machineName, collectionName)
                    holder.childRecyclerView.visibility = View.VISIBLE
                    holder.progressBar.visibility = View.GONE
                    notifyItemChanged(position)
                }
            } else {
                holder.progressBar.visibility = View.GONE
            }
        }
    }

    class ParentRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val maintenanceType: TextView = itemView.findViewById(R.id.maintenance_type)
        val arrowImage: ImageView = itemView.findViewById(R.id.left_arrow)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.tasks_list)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    }
}
