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
            Toast.makeText(this, "Generate Report", Toast.LENGTH_SHORT).show()
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
                        val tasks = document.get("tasks") as? List<String> ?: emptyList()
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

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
}
