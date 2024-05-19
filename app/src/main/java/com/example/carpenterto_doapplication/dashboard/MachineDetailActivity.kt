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
import com.google.firebase.firestore.FirebaseFirestore

class MachineDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_detail)

        val machineId = intent.getIntExtra("machineId", 0)
        val machineName = intent.getStringExtra("machineName") ?: "Unknown"
        val progressState = intent.getStringExtra("progressState") ?: "Unknown"
        val progressNumber = intent.getIntExtra("progressNumber", 0)
        val tasks = intent.getStringArrayListExtra("tasks") ?: arrayListOf()
        val tasksCompleted = intent.getBooleanArrayExtra("tasksCompleted") ?: BooleanArray(tasks.size)

        val tvMachineName = findViewById<TextView>(R.id.machine_name)
        tvMachineName.text = machineName

        val recyclerView = findViewById<RecyclerView>(R.id.checklist_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChecklistAdapter(tasks, tasksCompleted, machineId)

        val btnGenerateReport = findViewById<Button>(R.id.generate_report_button)
        btnGenerateReport.setOnClickListener {
            Toast.makeText(this, "Generate Report Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
