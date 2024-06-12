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
import com.example.carpenterto_doapplication.adapter.ParentReportDetailAdapter
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
    private lateinit var parentReportAdapter: ParentReportDetailAdapter
    private lateinit var tasksCompletedData: ArrayList<TasksCompletedModel>

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
        val maintenanceTypeMap = mapOf(
            "Daily Maintenance" to "dailyMaintenance",
            "Monthly Maintenance" to "monthlyMaintenance",
            "As Needed Maintenance" to "asNeededMaintenance",
            "Suggested Maintenance" to "suggestedMaintenance"
        )
        var remainingQueries = maintenanceTypeMap.size

        for ((maintenanceTitle, maintenanceType) in maintenanceTypeMap) {
            getTasksCompletedDataFromFirebase(maintenanceTitle, maintenanceType) {
                remainingQueries--
                if (remainingQueries == 0) {
                    setupRecyclerView()
                }
            }
        }
    }

    private fun getTasksCompletedDataFromFirebase(maintenanceTitle: String, maintenanceType: String, onComplete: () -> Unit) {
        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .collection(maintenanceType)
            .get()
            .addOnSuccessListener { documents ->
                val tasksCompleted = documents.map { document ->
                    val originalTask = document.toObject(TasksCompletedModel::class.java)
                    // Create a new instance with the desired maintenanceType
                    TasksCompletedModel(
                        maintenanceType = maintenanceTitle,
                        arrowImage = originalTask.arrowImage,
                        tasksCompleted = originalTask.tasksCompleted
                    )
                }

                if (tasksCompleted.isNotEmpty()) {
                    tasksCompletedData.addAll(tasksCompleted)
                } else {
                    tasksCompletedData.add(TasksCompletedModel(maintenanceType = maintenanceTitle, arrowImage = 0, tasksCompleted = emptyList()))
                }

                onComplete()
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Something went wrong")
                onComplete()
            }
    }

    private fun setupRecyclerView() {
        Log.d("TAG", "setupRecyclerView: $tasksCompletedData")
        parentReportAdapter = ParentReportDetailAdapter(tasksCompletedData)
        parentRecyclerView.adapter = parentReportAdapter
    }

    private fun downloadReport() {
        val hssfWorkbook = HSSFWorkbook()
        val hssfSheet: HSSFSheet = hssfWorkbook.createSheet("Machine Report")

        // Create header
        var row = hssfSheet.createRow(0)
        row.createCell(0).setCellValue("MAINTENANCE REPORT")

        // Create name, machine name, and other details
        row = hssfSheet.createRow(2)
        row.createCell(0).setCellValue("Name: $fullName")
        row.createCell(4).setCellValue("Date Generated: $reportDate")

        row = hssfSheet.createRow(3)
        row.createCell(0).setCellValue("Machine Name: $machineName")
        row.createCell(4).setCellValue("Time Generated: $reportTime")

        row = hssfSheet.createRow(4)
        row.createCell(0).setCellValue("Report State: $progressState")

        row = hssfSheet.createRow(5)
        row.createCell(0).setCellValue("Report Progress ${progressNumber.toInt()}% Done")

        // Create maintenance sections
        var rowIndex = 7

        for (taskCompleted in tasksCompletedData) {
            row = hssfSheet.createRow(rowIndex++)
            row.createCell(0).setCellValue(taskCompleted.maintenanceType)

            if (taskCompleted.tasksCompleted.isEmpty()) {
                row = hssfSheet.createRow(rowIndex++)
                row.createCell(0).setCellValue("[Not Started]")
            } else {
                taskCompleted.tasksCompleted.forEach { task ->
                    row = hssfSheet.createRow(rowIndex++)
                    row.createCell(0).setCellValue(task)
                    row.createCell(1).setCellValue("\u2611") // Placeholder for checkbox
                }
            }

            // Add a blank row after each section
            rowIndex++
        }

        // Save the file
        val sanitizedMachineName = machineName.replace(" ", "_")
        val sanitizedFullName = fullName.replace(" ", "_")
        val sanitizedTime = reportTime.replace(":", ".").replace(" ", "_")
        val reportFileName = "$sanitizedMachineName-Report-$sanitizedFullName-$sanitizedTime.xls"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val reportFile = File(downloadsDir, reportFileName)

        try {
            FileOutputStream(reportFile).use { fileOutputStream ->
                hssfWorkbook.write(fileOutputStream)
                Thread.sleep(1000)
                Toast.makeText(this, "Report Downloaded", Toast.LENGTH_SHORT).show()
                setInProgressBackground(false)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            UiUtil.showToast(this, "Error saving report")
            setInProgressBackground(false)
        }
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }
}
