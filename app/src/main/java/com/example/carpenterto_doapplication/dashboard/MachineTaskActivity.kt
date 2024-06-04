package com.example.carpenterto_doapplication.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.example.carpenterto_doapplication.databinding.ActivityMachineTaskBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import java.text.DateFormat
import java.util.Calendar

class MachineTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachineTaskBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter
    private lateinit var taskData: ArrayList<TaskModel>

    private lateinit var machineId: String
    private lateinit var machineName: String

    private val tasks = mutableListOf<String>()
    private val tasksCompleted = mutableListOf<Boolean>()
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
        getDataFromFirebase()

        binding.saveProgressButton.setOnClickListener {
            Toast.makeText(this, "Saved Progress", Toast.LENGTH_SHORT).show()
        }

        binding.generateReportButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                generateReport()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
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

    private fun getDataFromFirebase() {
        taskData = ArrayList()
        retrieveTasksFromCollection("dailyMaintenance")
        retrieveTasksFromCollection("monthlyMaintenance")
        retrieveTasksFromCollection("asNeededMaintenance")
    }

    private fun retrieveTasksFromCollection(collectionName: String) {
        setInProgress(true)
        Firebase.firestore
            .collection("tasks")
            .document(userId)
            .collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val tasks = document.get("tasksCompleteName") as? List<String> ?: emptyList()
                        val tasksCompleted = document.get("tasksCompleted") as? List<Boolean> ?: emptyList()
                        val task = TaskModel(tasks, tasksCompleted)
                        Log.d("Firestore", "Task: $task")
                        taskData.add(task)
                    }
                    setInProgress(false)
                    setDataToRecyclerView()
                } else {
                    Log.d("Firestore", "No documents found in $collectionName collection.")
                    setInProgress(false)
                }
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(this, e.localizedMessage ?: "Something went wrong")
                setInProgress(false)
            }
    }

    private fun setDataToRecyclerView() {
        checklistAdapter = ChecklistAdapter(tasks, tasksCompleted.toBooleanArray(), machineId)
        recyclerView.adapter = checklistAdapter
    }

    private fun generateReport() {
//        val workbook: Workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Sheet1")
//
//        // Create the header row
//        val headerRow: Row = sheet.createRow(0)
//        headerRow.createCell(0).setCellValue("Name: Bryan Jesus Mangapit")
//        headerRow.createCell(2).setCellValue("Date Generated: 06/01/2024")
//
//        val headerRow2: Row = sheet.createRow(1)
//        headerRow2.createCell(0).setCellValue("Machine Report: 0001")
//        headerRow2.createCell(2).setCellValue("Time Generated: 10:24PM")
//
//        val headerRow3: Row = sheet.createRow(2)
//        headerRow3.createCell(0).setCellValue("Machine Name: $machineName")
//
//        // Skip a row for spacing
//        sheet.createRow(3)
//
//        // Daily Maintenance
//        val maintenanceRow: Row = sheet.createRow(4)
//        maintenanceRow.createCell(0).setCellValue("Daily Maintenance")
//
//        // Populate data from taskData
//        var rowIndex = 5
//        for (task in taskData) {
//            for (i in task.tasks.indices) {
//                val dataRow: Row = sheet.createRow(rowIndex++)
//                dataRow.createCell(0).setCellValue(task.tasks[i])
//                dataRow.createCell(1).setCellValue(if (task.tasksCompleted[i]) "Yes" else "No")
//                dataRow.createCell(2).setCellValue(DateFormat.getDateTimeInstance().format(Calendar.getInstance().time))
//            }
//        }
//
//        val fileName = "Maintenance_Report.xlsx"
//        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
//
//        try {
//            FileOutputStream(filePath).use { outputStream ->
//                workbook.write(outputStream)
//                Toast.makeText(this, "Generated Report: $filePath", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateReport()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
}
