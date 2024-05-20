package com.example.carpenterto_doapplication.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.MachineAdapter
import com.example.carpenterto_doapplication.data_model.MachineModel
import com.google.firebase.firestore.FirebaseFirestore

class MachineFragment : Fragment(), MachineAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var machineAdapter: MachineAdapter
    private val taskList = mutableListOf<MachineModel>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machine, container, false)

        recyclerView = view.findViewById(R.id.machine_list)
        machineAdapter = MachineAdapter(requireActivity(), taskList, this)
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = machineAdapter

        prepareMachineListData()

        return view
    }

    private fun prepareMachineListData() {
        val machine1Tasks = mutableListOf("PM review", "Design team critique session", "Engineer Review")
        val machine2Tasks = mutableListOf("Development Review", "App Review", "Carpentry")
        // ... add more tasks for other machines

        taskList.add(MachineModel(1, "Forklift", "In Progress", 1, machine1Tasks))
        taskList.add(MachineModel(2, "Excavator", "Pending", 2, machine2Tasks))
        // ... add more machines

        machineAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        val selectedMachine = taskList[position]
        val intent = Intent(requireContext(), MachineTaskActivity::class.java).apply {
            putExtra("machineId", selectedMachine.machineId)
            putExtra("machineName", selectedMachine.machineName)
            putStringArrayListExtra("tasks", ArrayList(selectedMachine.tasks))
        }
        startActivity(intent)
    }
}
