package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.example.carpenterto_doapplication.databinding.ActivityMachineTaskBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.Calendar

class MachineTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachineTaskBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter
    private lateinit var taskData: ArrayList<TaskModel>
    private lateinit var machineId: String
    private lateinit var machineName: String
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMachineTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        machineId = intent.getIntExtra("machine_id", 0).toString()
        machineName = intent.getStringExtra("machine_name") ?: ""
        binding.machineName.text = machineName

        recyclerView = binding.checklistRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bindDate()
        setupData()

        binding.generateReportButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to generate report?")
            builder.setMessage("Please make sure you have completed all the tasks for this maintenance.")
            builder.setPositiveButton("Generate") { _, _ ->
                setReportDataToFirebase()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.checklistRecyclerView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.checklistRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupData() {
        taskData = ArrayList()
        getMachineTaskDataFromFirebase("dailyMaintenance")
        getMachineTaskDataFromFirebase("monthlyMaintenance")
        getMachineTaskDataFromFirebase("asNeededMaintenance")
        getMachineTaskDataFromFirebase("suggestedMaintenance")
    }

    private fun getMachineTaskDataFromFirebase(collectionName: String) {
        setInProgress(true)
        Firebase.firestore
            .collection("tasks")
            .document(userId)
            .collection(collectionName)
            .document(machineName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tasks = document.get("tasks") as? List<String> ?: emptyList()
                    val tasksCompleted = document.get("tasksCompleted") as? List<Boolean> ?: emptyList()
                    val task = TaskModel(tasks, tasksCompleted)
                    taskData.add(task)
                    setInProgress(false)
                    setDataToRecyclerView()
                } else {
                    setInProgress(false)
                }
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Something went wrong")
                setInProgress(false)
            }
    }

    private fun setDataToRecyclerView() {
        checklistAdapter = ChecklistAdapter(taskData, userId, machineName)
        recyclerView.adapter = checklistAdapter
        checklistAdapter.notifyDataSetChanged()
    }

    private fun setReportDataToFirebase() {
        val calendar = Calendar.getInstance()
        val reportDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.time)
        val reportTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)

        Firebase.firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    val fullName = userDocument.getString("fullName") ?: "Unknown User"

                    // Calculate progress
                    val completedTasks = taskData.flatMap { taskModel ->
                        taskModel.tasks.zip(taskModel.tasksCompleted)
                            .filter { it.second }
                            .map { it.first }
                    }
                    val progressState = if (completedTasks.size == taskData.sumOf { it.tasks.size }) {
                        "Completed"
                    } else {
                        "In Progress"
                    }
                    val progressPercentage = (completedTasks.size.toDouble() / taskData.sumOf { it.tasks.size }) * 100

                    // Create report map
                    val reportData = mapOf(
                        "userId" to userId,
                        "fullName" to fullName,
                        "machineName" to machineName,
                        "reportDate" to reportDate,
                        "reportTime" to reportTime,
                        "progressState" to progressState,
                        "progressNumber" to progressPercentage,
                    )

                    // Add report to `reports` collection
                    val reportsCollection = Firebase.firestore.collection("reports")
                    reportsCollection.add(reportData).addOnSuccessListener { reportRef ->
                        UiUtil.showToast(this, "Report data saved to Firebase")

                        // For each maintenance type, save completed tasks to sub-collections
                        val maintenanceTypes = listOf("dailyMaintenance", "monthlyMaintenance", "asNeededMaintenance", "suggestedMaintenance")

                        for (maintenanceType in maintenanceTypes) {
                            Firebase.firestore
                                .collection("tasks")
                                .document(userId)
                                .collection(maintenanceType)
                                .document(machineName)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val tasks = document.get("tasks") as? List<String> ?: emptyList()
                                        val tasksCompleted = document.get("tasksCompleted") as? List<Boolean> ?: emptyList()
                                        val completedTasksForType = tasks.zip(tasksCompleted)
                                            .filter { it.second }
                                            .map { it.first }

                                        val maintenanceData = mapOf(
                                            "tasksCompleted" to completedTasksForType
                                        )

                                        reportRef.collection(maintenanceType).add(maintenanceData)
                                            .addOnSuccessListener {
                                                UiUtil.showToast(this, "$maintenanceType tasks saved to report")
                                            }
                                            .addOnFailureListener { e ->
                                                UiUtil.showToast(this, e.localizedMessage ?: "Failed to save $maintenanceType tasks")
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    UiUtil.showToast(this, e.localizedMessage ?: "Failed to retrieve $maintenanceType tasks")
                                }
                        }

                        getReportDataFromFirebase()
                    }.addOnFailureListener { e ->
                        UiUtil.showToast(this, e.localizedMessage ?: "Failed to save report data")
                    }
                }
            }
    }

    private fun getReportDataFromFirebase() {
        Firebase.firestore
            .collection("reports")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val reportDocument = querySnapshot.documents.first()
                    val reportData = reportDocument.data?.toMutableMap() ?: mutableMapOf()

                    val maintenanceTypes = listOf("dailyMaintenance", "monthlyMaintenance", "asNeededMaintenance", "suggestedMaintenance")

                    val maintenanceDataMap = mutableMapOf<String, List<String>>()

                    maintenanceTypes.forEach { maintenanceType ->
                        reportDocument.reference.collection(maintenanceType).get()
                            .addOnSuccessListener { collectionSnapshot ->
                                if (!collectionSnapshot.isEmpty) {
                                    val completedTasks = collectionSnapshot.documents.flatMap { document ->
                                        document.get("tasksCompleted") as? List<String> ?: emptyList()
                                    }
                                    maintenanceDataMap[maintenanceType] = completedTasks
                                }
                                if (maintenanceDataMap.size == maintenanceTypes.size) {
                                    // All maintenance types have been processed
                                    reportData.putAll(maintenanceDataMap as Map<out String, Any>)
                                    generateReport(reportData)
                                }
                            }
                            .addOnFailureListener { e ->
                                UiUtil.showToast(this, e.localizedMessage ?: "Failed to retrieve $maintenanceType data")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Failed to retrieve report data")
            }
    }

    private fun generateReport(reportData: Map<String, Any>?) {
        reportData?.let {
            val fullName = it["fullName"] as? String ?: "Unknown User"
            val machineName = it["machineName"] as? String ?: ""
            val reportDate = it["reportDate"] as? String ?: ""
            val reportTime = it["reportTime"] as? String ?: ""
            val progressState = it["progressState"] as? String ?: ""
            val progressNumber = it["progressNumber"] as? Double ?: 0.0

            val dailyTasks = it["dailyMaintenance"] as? List<String> ?: emptyList()
            val monthlyTasks = it["monthlyMaintenance"] as? List<String> ?: emptyList()
            val asNeededTasks = it["asNeededMaintenance"] as? List<String> ?: emptyList()
            val suggestedTasks = it["suggestedMaintenance"] as? List<String> ?: emptyList()

            val hssfWorkbook = HSSFWorkbook()
            val hssfSheet: HSSFSheet = hssfWorkbook.createSheet("Machine Report")

            // Create styles
            val headerStyle = hssfWorkbook.createCellStyle().apply {
                setFont(hssfWorkbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 14
                })
            }

            val boldStyle = hssfWorkbook.createCellStyle().apply {
                setFont(hssfWorkbook.createFont().apply {
                    bold = true
                })
            }

            val dateStyle = hssfWorkbook.createCellStyle().apply {
                setFont(hssfWorkbook.createFont().apply {
                    italic = true
                })
            }

            // Create header
            var row = hssfSheet.createRow(0)
            var cell = row.createCell(0)
            cell.setCellValue("MAINTENANCE REPORT")
            cell.setCellStyle(headerStyle)

            // Create name, machine name, and other details
            row = hssfSheet.createRow(2)
            row.createCell(0).apply {
                setCellValue("Name: $fullName")
                setCellStyle(boldStyle)
            }

            row.createCell(4).apply {
                setCellValue("Date Generated: $reportDate")
                setCellStyle(dateStyle)
            }

            row = hssfSheet.createRow(3)
            row.createCell(0).apply {
                setCellValue("Machine Name: $machineName")
                setCellStyle(boldStyle)
            }

            row.createCell(4).apply {
                setCellValue("Time Generated: $reportTime")
                setCellStyle(dateStyle)
            }

            row = hssfSheet.createRow(4)
            row.createCell(0).setCellValue("Report State: $progressState")

            row = hssfSheet.createRow(5)
            row.createCell(0).setCellValue("Report Progress: $progressNumber")

            // Create maintenance sections
            val sections = mapOf(
                "Daily Maintenance" to dailyTasks,
                "Monthly Maintenance" to monthlyTasks,
                "As Needed Maintenance" to asNeededTasks,
                "Suggested Maintenance" to suggestedTasks
            )
            var rowIndex = 7

            for ((section, tasks) in sections) {
                row = hssfSheet.createRow(rowIndex++)
                row.createCell(0).apply {
                    setCellValue(section)
                    setCellStyle(boldStyle)
                }

                tasks.forEach { task ->
                    row = hssfSheet.createRow(rowIndex++)
                    row.createCell(0).setCellValue(task)
                    row.createCell(1).setCellValue("") // Placeholder for checkbox
                }

                // Add a blank row after each section
                rowIndex++
            }

            // Save the file
            val sanitizedMachineName = machineName.replace(" ", "_")
            val sanitizedFullName = fullName.replace(" ", "_")
            val reportFileName = "$sanitizedMachineName-Report-$sanitizedFullName.xls"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val reportFile = File(downloadsDir, reportFileName)

            try {
                FileOutputStream(reportFile).use { fileOutputStream ->
                    hssfWorkbook.write(fileOutputStream)
                    UiUtil.showToast(this, "Report Generated to Downloads!")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                UiUtil.showToast(this, "Error saving report")
            }
        }
    }


    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }
}
