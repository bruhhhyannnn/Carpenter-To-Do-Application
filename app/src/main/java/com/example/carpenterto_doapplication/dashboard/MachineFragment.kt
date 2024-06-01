package com.example.carpenterto_doapplication.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var machineList: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machine, container, false)

//        machineList = resources.getStringArray(R.array.machineList)
//        UiUtil.showToast(this.requireContext(), machineList[0])


        recyclerView = view.findViewById(R.id.machine_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        getData()

        prepareMachineData()

        return view
    }

    private fun getData() {


        progressStateList = arrayOf(
            "Not Started",
            "In Progress",
            "Completed"
        )
    }

    private fun prepareMachineData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        
        machineData = ArrayList()
        machineData.add(MachineModel(userId, 1, "Forklift", "In Progress", 1))
        machineData.add(MachineModel(userId, 2, "Excavator", "Not Started", 0))
        machineData.add(MachineModel(userId, 3, "Compactor", "Not Started", 0))
        machineData.add(MachineModel(userId, 4, "Dump-Truck", "Not Started", 0))
        machineData.add(MachineModel(userId, 5, "Flat-Bed-Truck", "Not Started", 0))
        machineData.add(MachineModel(userId, 6, "Transit-Mixer", "Not Started", 0))
        machineData.add(MachineModel(userId, 7, "Grader", "Not Started", 0))
        machineData.add(MachineModel(userId, 8, "Bulldozer", "Not Started", 0))
        machineData.add(MachineModel(userId, 9, "Crane", "Not Started", 0))
        machineData.add(MachineModel(userId, 10, "Road-Roller", "Not Started", 0))
        machineData.add(MachineModel(userId, 11, "Loader", "Not Started", 0))
        machineData.add(MachineModel(userId, 12, "Backhoe", "Not Started", 0))
        machineData.add(MachineModel(userId, 13, "One-Bagger", "Not Started", 0))

        // Populating data inside the RecyclerView
        machineAdapter = MachineAdapter(machineData)
        recyclerView.adapter = machineAdapter

        machineAdapter.onItemClick = { machine ->
            val intent = Intent(requireContext(), MachineTaskActivity::class.java).apply {
                putExtra("machine_id", machine.machineId)
                putExtra("machine_name", machine.machineName)
            }
            startActivity(intent)

            Toast.makeText(context, "Machine Name: ${machine.machineName} Machine Id: ${machine.machineId}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDataFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("machines")
            .document(userId)
            .collection(1.toString())
            .get()


        val machineData = ArrayList<MachineModel>()

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
