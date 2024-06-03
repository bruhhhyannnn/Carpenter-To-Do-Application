package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.dashboard.MachineTaskActivity
import com.google.firebase.firestore.FirebaseFirestore

class ChecklistAdapter(
    private val tasks: List<String>,
    private val tasksCompleted: BooleanArray,
    private val machineId: String
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.taskCheckBox.isChecked = tasksCompleted[position]

        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            tasksCompleted[position] = isChecked
            saveProgressToFirebase()
        }
    }

    private fun saveProgressToFirebase() {
        val db = FirebaseFirestore.getInstance()
        val progressData = hashMapOf(
            "tasks_completed" to tasksCompleted.toList() // Convert to list
        )

        db.collection("tasks")
            .document(machineId)
            .set(progressData)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
