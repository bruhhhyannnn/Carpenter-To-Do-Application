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

class MachineTaskActivity : AppCompatActivity() {

    private lateinit var tasks: List<String>
    private lateinit var tasksCompleted: BooleanArray
    private lateinit var checklistAdapter: ChecklistAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_task)

        val machineName = intent.getStringExtra("machineName") ?: "Unknown"
        tasks = intent.getStringArrayListExtra("tasks") ?: arrayListOf()
        tasksCompleted = BooleanArray(tasks.size) { false }

        val tvMachineName = findViewById<TextView>(R.id.machine_name)
        tvMachineName.text = machineName

        val recyclerView = findViewById<RecyclerView>(R.id.checklist_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        checklistAdapter = ChecklistAdapter(tasks, tasksCompleted)
        recyclerView.adapter = checklistAdapter

        val btnGenerateReport = findViewById<Button>(R.id.generate_report_button)
        btnGenerateReport.setOnClickListener {
            Toast.makeText(this, "Generate Report Clicked", Toast.LENGTH_SHORT).show()
        }

        val btnSaveProgress = findViewById<Button>(R.id.save_progress_button)
        btnSaveProgress.setOnClickListener {
            saveProgressToFirestore(machineName)
        }
    }

    private fun saveProgressToFirestore(machineName: String) {
        val machineRef = db.collection("users").document(userId)
            .collection("reports").document(machineName)

        val tasksCompletedList = tasksCompleted.toList()
        val data = hashMapOf(
            "machineName" to machineName,
            "tasksCompleted" to tasksCompletedList
        )

        machineRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Progress Saved Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving progress: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
