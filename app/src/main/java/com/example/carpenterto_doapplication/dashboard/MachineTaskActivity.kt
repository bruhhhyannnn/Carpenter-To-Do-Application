package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.example.carpenterto_doapplication.databinding.ActivityMachineTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.util.Calendar

class MachineTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachineTaskBinding

    private lateinit var checklistAdapter: ChecklistAdapter

    private lateinit var machineId: String
    private lateinit var machineName: String
    private val tasks = mutableListOf<String>()
    private val tasksCompleted = mutableListOf<Boolean>()

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMachineTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindDate()

        machineId = intent.getIntExtra("machine_id", 0).toString()
        machineName = intent.getStringExtra("machine_name") ?: ""

        binding.machineName.text = machineName

        binding.checklistRecyclerView.layoutManager = LinearLayoutManager(this)

        tasks.addAll(listOf("Task 1", "Task 2", "Task 3"))
        tasksCompleted.addAll(List(tasks.size) { false })

        checklistAdapter = ChecklistAdapter(tasks, tasksCompleted.toBooleanArray(), machineId)
        binding.checklistRecyclerView.adapter = checklistAdapter

        binding.saveProgressButton.setOnClickListener {
            saveProgressToFirestore()
        }

        binding.generateReportButton.setOnClickListener {
            Toast.makeText(this, "Report Generated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProgressToFirestore() {
        val progressData = hashMapOf(
            "machine_name" to machineName,
            "tasks_completed" to tasksCompleted
        )

        db.collection("machines")
            .document(userId)
            .set(progressData)
            .addOnSuccessListener {
                Toast.makeText(this, "Progress Saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Save Progress", Toast.LENGTH_SHORT).show()
            }

        db.collection("users").document(userId).collection("reports").document(machineId)
            .set(progressData)
            .addOnSuccessListener {
                Toast.makeText(this, "Progress Saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Save Progress", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bindDate() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        binding.dateTodayText.text = "Today: " + dateFormat
    }
}
