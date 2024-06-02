package com.example.carpenterto_doapplication.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.MachineAdapter
import com.example.carpenterto_doapplication.data_model.MachineModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MachineFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var machineData: ArrayList<MachineModel>
    private lateinit var machineAdapter: MachineAdapter

    lateinit var progressStateList: Array<String>
    lateinit var progressBar : ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machine, container, false)

        progressBar = view.findViewById(R.id.progress_bar)

        recyclerView = view.findViewById(R.id.machine_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        getDataFromFirebase()

        return view
    }

    fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun getDataFromFirebase() {
        machineData = ArrayList()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userMachinesRef = Firebase.firestore
            .collection("machines")
            .document(userId)
            .collection("userMachines")

        userMachinesRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val machineId = document.getLong("machineId")?.toInt() ?: 0
                        val machineName = document.getString("machineName") ?: ""
                        val progressState = document.getString("progressState") ?: ""
                        val progressNumber = document.getLong("progressNumber")?.toInt() ?: 0

                        val machine = MachineModel(
                            machineId,
                            machineName,
                            progressState,
                            progressNumber
                        )
                        machineData.add(machine)
                    }
                    setInProgress(false)
                    setDataToRecyclerView()
                } else {
                    setInProgress(false)
                    Log.d("Firestore", "No documents found in userMachines collection.")
                }
            }
            .addOnFailureListener { e ->
                UiUtil.showToast(requireContext(), e.localizedMessage ?: "Something went wrong")
                setInProgress(false)
            }
    }

    private fun setDataToRecyclerView() {
        machineAdapter = MachineAdapter(machineData)
        recyclerView.adapter = machineAdapter

        machineAdapter.onItemClick = { machine ->
            val intent = Intent(requireContext(), MachineTaskActivity::class.java).apply {
                putExtra("machine_id", machine.machineId)
                putExtra("machine_name", machine.machineName)
            }
            startActivity(intent)
        }
    }

//    private fun updateProgress() {
//        val completedCount = tasksCompleted.count { it }
//        val progressPercentage = (completedCount.toDouble() / tasksCompleted.size) * 100
//        val progressState = when {
//            completedCount == 0 -> "Not started"
//            completedCount == tasksCompleted.size -> "Completed"
//            else -> "In Progress"
//        }
//
//        // Update progress and state UI here if needed
//    }
}
