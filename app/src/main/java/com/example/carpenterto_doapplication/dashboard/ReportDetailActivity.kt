package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.adapter.ReportDetailAdapter
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.example.carpenterto_doapplication.data_model.TasksCompletedModel
import com.example.carpenterto_doapplication.databinding.ActivityReportDetailBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.Calendar

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailBinding
    private lateinit var parentRecyclerView: RecyclerView
    private lateinit var tasksCompletedData: ArrayList<TasksCompletedModel>
    private lateinit var reportAdapter: ReportDetailAdapter

    private lateinit var reportId: String
    private lateinit var fullName: String
    private lateinit var machineName: String
    private var progressNumber: Double = 0.0
    private lateinit var progressState: String
    private lateinit var reportDate: String
    private lateinit var reportTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportId = intent.getStringExtra("reportId") ?: ""
        fullName = intent.getStringExtra("fullName") ?: ""
        machineName = intent.getStringExtra("machineName") ?: ""
        progressNumber = intent.getDoubleExtra("progressNumber", 0.0)
        progressState = intent.getStringExtra("progressState") ?: ""
        reportDate = intent.getStringExtra("reportDate") ?: ""
        reportTime = intent.getStringExtra("reportTime") ?: ""

        parentRecyclerView = binding.recyclerView
        parentRecyclerView.setHasFixedSize(true)
        parentRecyclerView.layoutManager = LinearLayoutManager(this)

        bindDate()
        bindReportData()

        binding.redownloadReportButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to re-download report?")
            builder.setPositiveButton("Download") { _, _ ->
                Toast.makeText(this, "Downloading Report...", Toast.LENGTH_SHORT).show()
                setInProgressBackground(true)
                downloadReport()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setInProgressBackground(inProgress: Boolean) {
        if (inProgress) {
            binding.loadingBackground.visibility = View.VISIBLE
            binding.centerProgressBar.visibility = View.VISIBLE
            binding.note.visibility = View.VISIBLE
            binding.mainActivityLayout.visibility = View.GONE
        } else {
            binding.loadingBackground.visibility = View.GONE
            binding.centerProgressBar.visibility = View.GONE
            binding.note.visibility = View.GONE
            binding.mainActivityLayout.visibility = View.VISIBLE
        }
    }

    private fun bindReportData() {
        binding.machineName.text = machineName
        binding.progressNumber.text = "${progressNumber.toInt()}%"
        binding.progressState.text = progressState
        binding.reportDate.text = reportDate
        binding.reportTime.text = reportTime

        setTasksCompletedData()
    }

    private fun setTasksCompletedData() {
        tasksCompletedData = ArrayList()
        val combinedTasks = ArrayList<String>()

        val collections = listOf("dailyMaintenance", "monthlyMaintenance", "asNeededMaintenance", "suggestedMaintenance")
        var completedRequests = 0

        for (collection in collections) {
            getTasksCompletedDataFromFirebase(collection) { tasks ->
                combinedTasks.addAll(tasks)
                completedRequests++

                if (completedRequests == collections.size) {
                    tasksCompletedData.add(TasksCompletedModel(combinedTasks))
                    setupRecyclerView()
                }
            }
        }
    }

    private fun getTasksCompletedDataFromFirebase(collectionName: String, callback: (List<String>) -> Unit) {
        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = ArrayList<String>()
                for (document in documents) {
                    val task = document.toObject(TasksCompletedModel::class.java)
                    if (task.tasksCompleted.isNotEmpty()) { // Check if the document is not empty
                        tasks.addAll(task.tasksCompleted) // Add each task to the list
                    }
                }
                callback(tasks) // Invoke the callback with the fetched tasks
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Something went wrong")
                callback(emptyList()) // Invoke the callback with an empty list on failure
            }
    }

    private fun setupRecyclerView() {
        val flattenedTasksList = tasksCompletedData.flatMap { it.tasksCompleted }
        reportAdapter = ReportDetailAdapter(flattenedTasksList)
        parentRecyclerView.adapter = reportAdapter
    }


    private fun downloadReport() {
////        val dailyTasks = tasksCompletedData.filter { it.type == "dailyMaintenance" }.map { it.task }
////        val monthlyTasks = tasksCompletedData.filter { it.type == "monthlyMaintenance" }.map { it.task }
////        val asNeededTasks = tasksCompletedData.filter { it.type == "asNeededMaintenance" }.map { it.task }
////        val suggestedTasks = tasksCompletedData.filter { it.type == "suggestedMaintenance" }.map { it.task }
//
//        val hssfWorkbook = HSSFWorkbook()
//        val hssfSheet: HSSFSheet = hssfWorkbook.createSheet("Machine Report")
//
//        // Create header
//        var row = hssfSheet.createRow(0)
//        row.createCell(0).setCellValue("MAINTENANCE REPORT")
//
//        // Create name, machine name, and other details
//        row = hssfSheet.createRow(2)
//        row.createCell(0).setCellValue("Name: $fullName")
//        row.createCell(4).setCellValue("Date Generated: $reportDate")
//
//        row = hssfSheet.createRow(3)
//        row.createCell(0).setCellValue("Machine Name: $machineName")
//        row.createCell(4).setCellValue("Time Generated: $reportTime")
//
//        row = hssfSheet.createRow(4)
//        row.createCell(0).setCellValue("Report State: $progressState")
//
//        row = hssfSheet.createRow(5)
//        row.createCell(0).setCellValue("Report Progress ${progressNumber.toInt()}% Done")
//
////        // Create maintenance sections
////        val sections = mapOf(
////            "Daily Maintenance" to dailyTasks,
////            "Monthly Maintenance" to monthlyTasks,
////            "As Needed Maintenance" to asNeededTasks,
////            "Suggested Maintenance" to suggestedTasks
////        )
////        var rowIndex = 7
////
////        for ((section, tasks) in sections) {
////            row = hssfSheet.createRow(rowIndex++)
////            row.createCell(0).setCellValue(section)
////
////            if (tasks.isEmpty()) {
////                row = hssfSheet.createRow(rowIndex++)
////                row.createCell(0).setCellValue("[Not Started]")
////            } else {
////                tasks.forEach { task ->
////                    row = hssfSheet.createRow(rowIndex++)
////                    row.createCell(0).setCellValue(task)
////                    row.createCell(1).setCellValue("\u2611") // Placeholder for checkbox
////                }
////            }
////
////            // Add a blank row after each section
////            rowIndex++
////        }
//
//        // Save the file
//        val sanitizedMachineName = machineName.replace(" ", "_")
//        val sanitizedFullName = fullName.replace(" ", "_")
//        val sanitizedTime = reportTime.replace(":", ".").replace(" ", "_")
//        val reportFileName = "$sanitizedMachineName-Report-$sanitizedFullName-$sanitizedTime.xls"
//        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val reportFile = File(downloadsDir, reportFileName)
//
//        try {
//            FileOutputStream(reportFile).use { fileOutputStream ->
//                hssfWorkbook.write(fileOutputStream)
//                Thread.sleep(1000)
//                UiUtil.showToast(this, "Finished Generating Report!")
//                setInProgressBackground(false)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            UiUtil.showToast(this, "Error saving report")
//            setInProgressBackground(false)
//        }
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }
}
