package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ChecklistAdapter
import com.example.carpenterto_doapplication.databinding.ActivityMachineDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class MachineDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityMachineDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMachineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val machineId = intent.getIntExtra("machineId", 0)
        val machineName = intent.getStringExtra("machineName") ?: "Unknown"
        val progressState = intent.getStringExtra("progressState") ?: "Unknown"
        val progressNumber = intent.getIntExtra("progressNumber", 0)
        val tasks = intent.getStringArrayListExtra("tasks") ?: arrayListOf()
        val tasksCompleted = intent.getBooleanArrayExtra("tasksCompleted") ?: BooleanArray(tasks.size)

        binding.machineName.text = machineName

        val recyclerView = findViewById<RecyclerView>(R.id.checklist_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChecklistAdapter(tasks, tasksCompleted, machineId)

        binding.generateReportButton.setOnClickListener {
            Toast.makeText(this, "Generate Report Clicked", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to generate report?")
            builder.setMessage("Please make sure you have completed all the tasks for this maintenance.")
            builder.setPositiveButton("Generate") { dialog, _ ->
                // this will generate the report by making a pdf file containing the checklist items that user have accomplished while
                // incorporating it with the users profile i.e., full name, username and date of creation
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.saveProgressButton.setOnClickListener {
            // implement the save progress button functionality by saving the checked lists and storing it to firebase firestore for a particular user
            //
        }
    }
}
