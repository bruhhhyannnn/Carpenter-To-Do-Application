package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChecklistAdapter(
    private val taskList: List<TaskModel>,
    private val userId: String,
    private val machineName: String
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.sumOf { it.tasks.size }
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val (task, taskCompleted) = getTaskAtPosition(position)

        holder.taskCheckBox.text = task
        holder.taskCheckBox.isChecked = taskCompleted

        // Remove any existing listener before setting a new one
        holder.taskCheckBox.setOnCheckedChangeListener(null)

        // Set a new listener for the checkbox
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            updateTaskCompletionStatus(position, isChecked)
            val message = if (isChecked) "$task checked" else "$task unchecked"
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTaskAtPosition(position: Int): Pair<String, Boolean> {
        var count = 0
        for (taskModel in taskList) {
            if (position < count + taskModel.tasks.size) {
                val localPosition = position - count
                return Pair(taskModel.tasks[localPosition], taskModel.tasksCompleted[localPosition])
            }
            count += taskModel.tasks.size
        }
        throw IndexOutOfBoundsException("Invalid position")
    }

    private fun updateTaskCompletionStatus(position: Int, isCompleted: Boolean) {
        var count = 0
        for (taskModel in taskList) {
            if (position < count + taskModel.tasks.size) {
                val localPosition = position - count
                val updatedTasksCompleted = taskModel.tasksCompleted.toMutableList()
                updatedTasksCompleted[localPosition] = isCompleted

                // Save the updated tasksCompleted list to Firebase
                saveProgressToFirebase(taskModel.tasks, updatedTasksCompleted)
                taskModel.tasksCompleted = updatedTasksCompleted // Update the original list to keep UI and data in sync
                return
            }
            count += taskModel.tasks.size
        }
    }

    private fun saveProgressToFirebase(tasks: List<String>, tasksCompleted: List<Boolean>) {
        // First, update the tasks collection
        Firebase.firestore
            .collection("tasks")
            .document(userId)
            .collection("dailyMaintenance") // Assuming all tasks are from dailyMaintenance
            .document(machineName)
            .update(mapOf("tasks" to tasks, "tasksCompleted" to tasksCompleted))

        // Calculate progress
        val completedCount = tasksCompleted.count { it }
        val progressPercentage = (completedCount.toDouble() / tasksCompleted.size) * 100
        val progressState = when {
            completedCount == 0 -> "Not started"
            completedCount == tasksCompleted.size -> "Completed"
            else -> "In Progress"
        }

        // Update the machines collection
        Firebase.firestore
            .collection("machines")
            .document(userId)
            .collection("userMachines")
            .whereEqualTo("machineName", machineName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.update(
                        mapOf(
                            "tasks" to tasks,
                            "tasksCompleted" to tasksCompleted,
                            "progressNumber" to progressPercentage,
                            "progressState" to progressState
                        )
                    )
                }
            }
    }

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
