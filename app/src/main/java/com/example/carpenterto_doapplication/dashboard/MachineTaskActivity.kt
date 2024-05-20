package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MachineTaskActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter
    private lateinit var saveProgressButton: Button
    private lateinit var generateReportButton: Button

    private lateinit var machineId: String
    private lateinit var machineName: String
    private val tasks = mutableListOf<String>()
    private val tasksCompleted = mutableListOf<Boolean>()

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_task)

        machineId = intent.getIntExtra("machine_id", 0).toString()
        machineName = intent.getStringExtra("machine_name") ?: ""

        findViewById<TextView>(R.id.machine_name).text = machineName

        recyclerView = findViewById(R.id.checklist_recycler_view)
        saveProgressButton = findViewById(R.id.save_progress_button)
        generateReportButton = findViewById(R.id.generate_report_button)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tasks.addAll(listOf("Task 1", "Task 2", "Task 3"))
        tasksCompleted.addAll(List(tasks.size) { false })

        checklistAdapter = ChecklistAdapter(tasks, tasksCompleted.toBooleanArray(), machineId)
        recyclerView.adapter = checklistAdapter

        saveProgressButton.setOnClickListener {
            saveProgressToFirestore()
        }

        generateReportButton.setOnClickListener {
            Toast.makeText(this, "Report Generated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProgressToFirestore() {
        val progressData = hashMapOf(
            "machine_name" to machineName,
            "tasks_completed" to tasksCompleted
        )

        db.collection("users").document(userId).collection("reports").document(machineId)
            .set(progressData)
            .addOnSuccessListener {
                Toast.makeText(this, "Progress Saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Save Progress", Toast.LENGTH_SHORT).show()
            }
    }
}