package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ParentChecklistAdapter
import com.example.carpenterto_doapplication.data_model.MaintenanceTypesModel
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
    private lateinit var parentRecyclerView: RecyclerView
    private lateinit var parentChecklistAdapter: ParentChecklistAdapter
    private lateinit var taskData: ArrayList<TaskModel>
    private lateinit var parentData: ArrayList<MaintenanceTypesModel>
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

        parentRecyclerView = binding.parentRecyclerView
        parentRecyclerView.setHasFixedSize(true)
        parentRecyclerView.layoutManager = LinearLayoutManager(this)

        bindDate()
        setupTasksData()

        binding.generateReportButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to generate report?")
            builder.setMessage("Please make sure you have completed all the tasks for this maintenance.")
            builder.setPositiveButton("Generate") { _, _ ->
                setInProgressBackground(true)
                setReportDataToFirebase()
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
            binding.mainActivityLayout.visibility = View.GONE
        } else {
            binding.loadingBackground.visibility = View.GONE
            binding.centerProgressBar.visibility = View.GONE
            binding.mainActivityLayout.visibility = View.VISIBLE
        }
    }

    private fun setupTasksData() {
        taskData = ArrayList() // Initialize taskData
        parentData = ArrayList()
        parentData.add(MaintenanceTypesModel("Daily Maintenance", R.drawable.icon_arrow_left, ArrayList(), false))
        parentData.add(MaintenanceTypesModel("Monthly Maintenance", R.drawable.icon_arrow_left, ArrayList(), false))
        parentData.add(MaintenanceTypesModel("As Needed Maintenance", R.drawable.icon_arrow_left, ArrayList(), false))
        parentData.add(MaintenanceTypesModel("Suggested Maintenance", R.drawable.icon_arrow_left, ArrayList(), false))

        // Initialize the adapter with the lambda function for data fetching
        parentChecklistAdapter = ParentChecklistAdapter(parentData, userId, machineName, ::getMachineTaskDataFromFirebase)
        parentRecyclerView.adapter = parentChecklistAdapter

        // Initial data fetching for the categories
        getMachineTaskDataFromFirebase("dailyMaintenance", 0)
        getMachineTaskDataFromFirebase("monthlyMaintenance", 1)
        getMachineTaskDataFromFirebase("asNeededMaintenance", 2)
        getMachineTaskDataFromFirebase("suggestedMaintenance", 3)
    }

    private fun getMachineTaskDataFromFirebase(collectionName: String, index: Int, callback: (TaskModel) -> Unit = {}) {
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
                    parentData[index].childItemList.add(task)
                    taskData.add(task) // Add task to taskData
                    setDataToRecyclerView()
                    callback(task)
                } else {
                }
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Something went wrong")
            }
    }

    private fun setDataToRecyclerView() {
        parentChecklistAdapter.notifyDataSetChanged()
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

                    // Add report to `reports` collection
                    val reportsCollection = Firebase.firestore.collection("reports")
                    val reportRef = reportsCollection.document() // Automatically generates a unique ID
                    val reportId = reportRef.id // Get the generated ID

                    // Create report map
                    val reportData = mapOf(
                        "reportId" to reportId,
                        "userId" to userId,
                        "fullName" to fullName,
                        "machineName" to machineName,
                        "reportDate" to reportDate,
                        "reportTime" to reportTime,
                        "progressState" to progressState,
                        "progressNumber" to progressPercentage
                    )

                    reportRef.set(reportData).addOnSuccessListener {
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
                                                Log.d("MachineTaskActivity", "$maintenanceType tasks saved to report")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("MachineTaskActivity", "Failed to save $maintenanceType tasks", e)
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    UiUtil.showToast(this, e.localizedMessage ?: "Failed to retrieve $maintenanceType tasks")
                                }
                        }

                        getReportDataFromFirebase()
                    }.addOnFailureListener { e ->
                        UiUtil.showToast(this, e.localizedMessage ?: "Failed to generate report")
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
            val sections = mapOf(
                "Daily Maintenance" to dailyTasks,
                "Monthly Maintenance" to monthlyTasks,
                "As Needed Maintenance" to asNeededTasks,
                "Suggested Maintenance" to suggestedTasks
            )
            var rowIndex = 7

            for ((section, tasks) in sections) {
                row = hssfSheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(section)

                if (tasks.isEmpty()) {
                    row = hssfSheet.createRow(rowIndex++)
                    row.createCell(0).setCellValue("[Not Started]")
                } else {
                    tasks.forEach { task ->
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
            val reportFileName = "$sanitizedMachineName-Report-$sanitizedFullName.xls"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val reportFile = File(downloadsDir, reportFileName)

            try {
                FileOutputStream(reportFile).use { fileOutputStream ->
                    hssfWorkbook.write(fileOutputStream)
                    setInProgressBackground(false)
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
