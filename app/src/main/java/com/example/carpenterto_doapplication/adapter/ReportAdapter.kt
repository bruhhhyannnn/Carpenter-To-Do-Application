package com.example.carpenterto_doapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.ReportModel
import com.example.carpenterto_doapplication.data_model.ReportTaskModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportAdapter(
    private val reportList: ArrayList<ReportModel>,

) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    val reportTaskList: ArrayList<ReportTaskModel> = TODO()
    val combinedTasks = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.machineName.text = report.machineName
        holder.reportDateGenerated.text = report.reportDate
        holder.reportTimeGenerated.text = report.reportTime

        var reportTaskList: ArrayList<ReportTaskModel> = ArrayList()
        holder.reportTasksRecycleView.setHasFixedSize(true)
        holder.reportTasksRecycleView.layoutManager = LinearLayoutManager(holder.itemView.context)
        getReportTasksDataFromFirebase(report.reportId, "dailyMaintenance")
        getReportTasksDataFromFirebase(report.reportId, "monthlyMaintenance")
        getReportTasksDataFromFirebase(report.reportId, "asNeededMaintenance")
        getReportTasksDataFromFirebase(report.reportId, "suggestedMaintenance")
        reportTaskList.add(ReportTaskModel(tasksCompleted = combinedTasks))
        holder.reportTasksRecycleView.adapter = ReportTasksAdapter(reportTaskList)

        holder.deleteButton.setOnClickListener {
            showAboutAlertDialog(holder.itemView.context, position)
        }
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.report_title)
        val reportDateGenerated: TextView = itemView.findViewById(R.id.report_date_generated)
        val reportTimeGenerated: TextView = itemView.findViewById(R.id.report_time_generated)
        val deleteButton: ImageView = itemView.findViewById(R.id.remove_button)

        val reportTasksRecycleView: RecyclerView = itemView.findViewById(R.id.report_tasks_recycler_view)
    }

    private fun showAboutAlertDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Do you want to remove this report?")
        builder.setMessage("This would remove the report only here in the application but doesn't delete the file.")
        builder.setPositiveButton("Remove") { dialog, _ ->
            deleteReport(position)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteReport(position: Int) {
        val report = reportList[position]
        val reportId = report.reportId

        // Remove the report from the list
        reportList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, reportList.size)

        // Reference to the document
        val documentRef = FirebaseFirestore.getInstance().collection("reports").document(reportId)

        fun deleteSubcollection(collectionPath: String, onComplete: () -> Unit) {
            val subcollectionRef = documentRef.collection(collectionPath)
            subcollectionRef.get().addOnSuccessListener { subcollectionSnapshot ->
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in subcollectionSnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().addOnSuccessListener {
                    onComplete()
                }.addOnFailureListener { e ->
                    // Handle the error
                }
            }
        }

        deleteSubcollection("dailyMaintenance") {
            deleteSubcollection("monthlyMaintenance") {
                deleteSubcollection("asNeededMaintenance") {
                    deleteSubcollection("suggestedMaintenance") {
                        documentRef.delete().addOnSuccessListener {
                            // Report successfully deleted
                        }.addOnFailureListener { e ->
                            // Handle the error
                        }
                    }
                }
            }
        }
    }

    private fun getReportTasksDataFromFirebase(reportId: String, maintenanceType: String) {
        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .collection(maintenanceType)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val taskModel = document.toObject(ReportTaskModel::class.java)
                        taskModel.let {
                            if (it.tasksCompleted.isNotEmpty()) {
                                combinedTasks.addAll(it.tasksCompleted)
                                Log.d("ReportFragment", "combinedTasks: $combinedTasks")
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }
}
