package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ReportAdapter
import com.example.carpenterto_doapplication.data_model.ReportModel
import com.example.carpenterto_doapplication.data_model.ReportTaskModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportData: ArrayList<ReportModel>
    private lateinit var reportTaskData: ArrayList<ReportTaskModel>
    private lateinit var reportAdapter: ReportAdapter

    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var reportsFound: TextView

    lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        progressBar = view.findViewById(R.id.progress_bar)
        reportsFound = view.findViewById(R.id.reports_found)
        recyclerView = view.findViewById(R.id.report_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        reportData = ArrayList()
        reportTaskData = ArrayList()
        reportAdapter = ReportAdapter(reportData, reportTaskData)
        recyclerView.adapter = reportAdapter

        getReportDataFromFirebase()

        return view
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun getReportDataFromFirebase() {
        setInProgress(true)
        Firebase.firestore
            .collection("reports")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    setInProgress(false)
                    reportsFound.visibility = View.VISIBLE
                } else {
                    reportsFound.visibility = View.GONE
                    for (document in documents) {
                        val reportModel = document.toObject(ReportModel::class.java)
                        reportModel.let {
                            reportData.add(it)
                            fetchTasksForReport(document.id, "dailyMaintenance")
                            fetchTasksForReport(document.id, "monthlyMaintenance")
                            fetchTasksForReport(document.id, "asNeededMaintenance")
                            fetchTasksForReport(document.id, "suggestedMaintenance")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                UiUtil.showToast(requireContext(), "Failed to fetch data from Firebase: ${exception.message}")
                setInProgress(false)
            }
    }

    private fun fetchTasksForReport(reportId: String, maintenanceType: String) {
        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .collection(maintenanceType)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val taskModel = document.toObject(ReportTaskModel::class.java)
                    if (taskModel.tasksCompleted.isNotEmpty()) {  // Check if tasksCompleted is not empty
                        taskModel.maintenanceType = maintenanceType
                        Log.d("ReportFragment", "TaskModel: $taskModel")
                        reportTaskData.add(taskModel)
                    }
                }
                reportAdapter.notifyDataSetChanged()
                setInProgress(false)
            }
            .addOnFailureListener { exception ->
                UiUtil.showToast(requireContext(), "Failed to fetch tasks from Firebase: ${exception.message}")
                setInProgress(false)
            }
    }
}
