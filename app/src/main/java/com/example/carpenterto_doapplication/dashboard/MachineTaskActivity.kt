package com.example.carpenterto_doapplication.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.example.carpenterto_doapplication.adapter.MachineAdapter
import com.example.carpenterto_doapplication.data_model.MachineModel
import com.example.carpenterto_doapplication.data_model.TaskModel
import com.example.carpenterto_doapplication.databinding.ActivityMachineTaskBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
//            getDataFromFirebase()
            Toast.makeText(this, "Saved Progress", Toast.LENGTH_SHORT).show()
        }

        binding.generateReportButton.setOnClickListener {
//            generateReport()
            Toast.makeText(this, "Report Generated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.checklistRecyclerView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.checklistRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun getDataFromFirebase() {




        tasks.addAll(listOf("Task 1", "Task 2", "Task 3"))
        tasksCompleted.addAll(List(tasks.size) { false })




        taskData = ArrayList()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userMachinesRef = Firebase.firestore
            .collection("tasks")
            .document(userId)
            .collection("dailyMaintenance")

        setInProgress(true)
        userMachinesRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        // get the data

                        val task = TaskModel(
                            tasksCompleted,
                            tasksCompletedName
                        )
                        taskData.add(task)
                    }
                    setInProgress(false)
                    setDataToRecyclerView()
                } else {
                    setInProgress(false)
                    Log.d("Firestore", "No documents found.")
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

    private fun saveProgressToFirebase() {
        // Save progress logic here
    }

    private fun generateReport() {
        // Generate report logic here
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: " + dateFormat
    }
}
