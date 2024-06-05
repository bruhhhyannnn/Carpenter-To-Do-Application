package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
            builder.setPositiveButton("Generate") { dialog, _ ->
                // generateReport()
                Toast.makeText(this, "Generate Report", Toast.LENGTH_SHORT).show()
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
        getDataFromFirebase("dailyMaintenance")
        getDataFromFirebase("monthlyMaintenance")
        getDataFromFirebase("asNeededMaintenance")
        getDataFromFirebase("suggestedMaintenance")
    }

    private fun getDataFromFirebase(collectionName: String) {
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
        // can you recheck whats happening here? i dont know what part of the code is wrong but
        // the checklist is just showing up the first index of the list and doesnt include the rest
        // when i try to Log the values of the data, it shows up the whole list of tasks
        // but when i try to run the app, it just shows the first index or data of the list
        checklistAdapter = ChecklistAdapter(taskData, userId, machineName)
        recyclerView.adapter = checklistAdapter
        checklistAdapter.notifyDataSetChanged()
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: $dateFormat"
    }
}
