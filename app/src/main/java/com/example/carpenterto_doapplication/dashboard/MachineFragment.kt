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
import com.google.firebase.auth.FirebaseAuth

class MachineFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var machineList: ArrayList<MachineModel>
    private lateinit var machineAdapter: MachineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machine, container, false)

        recyclerView = view.findViewById(R.id.machine_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        prepareMachineListData()

        return view
    }

    private fun prepareMachineListData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        
        machineList = ArrayList()
        machineList.add(MachineModel(userId, 1, "Forklift", "In Progress", 1))
        machineList.add(MachineModel(userId, 0, "Excavator", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Compactor", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Dump-Truck", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Flat-Bed-Truck", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Transit-Mixer", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Grader", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Bulldozer", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Crane", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Road-Roller", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Loader", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "Backhoe", "Not Started", 0))
        machineList.add(MachineModel(userId, 0, "One-Bagger", "Not Started", 0))

        machineAdapter = MachineAdapter(machineList)
        recyclerView.adapter = machineAdapter

        machineAdapter.onItemClick = { machine ->
            val intent = Intent(requireContext(), MachineTaskActivity::class.java).apply {
                putExtra("machine_id", machine.machineId)
                putExtra("machine_name", machine.machineName)
            }
            startActivity(intent)
        }
    }
}
