package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.google.firebase.firestore.FirebaseFirestore

class ChecklistAdapter(
    private val tasks: List<String>,
    private val tasksCompleted: BooleanArray,
    private val machineId: Int
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.checkBox.text = tasks[position]
        holder.checkBox.isChecked = tasksCompleted[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            tasksCompleted[position] = isChecked
            updateTaskCompletionInFirestore(position, isChecked)
        }
    }

    private fun updateTaskCompletionInFirestore(position: Int, isChecked: Boolean) {
        val machineRef = db.collection("machines").document(machineId.toString())
        machineRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val tasksCompleted = document.get("tasksCompleted") as? MutableList<Boolean>
                tasksCompleted?.set(position, isChecked)
                machineRef.update("tasksCompleted", tasksCompleted)
            }
        }
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
