package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.MyAdapter
import com.example.carpenterto_doapplication.data_model.TasksModel

class TasksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private var taskList = mutableListOf<TasksModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.machine_list)
        myAdapter = MyAdapter(taskList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

        // Prepare data
        prepareMachineListData()

        return view
    }

    private fun prepareMachineListData() {
        val machine1Tasks = mutableListOf("PM review, 19/01/2022", "Design team critique session", "Engineer Review")
        val machine2Tasks = mutableListOf("Development Review", "App Review", "Carpentry")
        val machine3Tasks = mutableListOf("Task 1", "Task 2", "Task 3")
        val machine4Tasks = mutableListOf("Task 4", "Task 5", "Task 6")
        val machine5Tasks = mutableListOf("Task 7", "Task 8", "Task 9")
        val machine6Tasks = mutableListOf("Task 10", "Task 11", "Task 12")
        val machine7Tasks = mutableListOf("Task 13", "Task 14", "Task 15")
        val machine8Tasks = mutableListOf("Task 16", "Task 17", "Task 18")
        val machine9Tasks = mutableListOf("Task 19", "Task 20", "Task 21")
        val machine10Tasks = mutableListOf("Task 22", "Task 23", "Task 24")
        val machine11Tasks = mutableListOf("Task 25", "Task 26", "Task 27")
        val machine12Tasks = mutableListOf("Task 28", "Task 29", "Task 30")
        val machine13Tasks = mutableListOf("Task 31", "Task 32", "Task 33")
        val machine14Tasks = mutableListOf("Task 34", "Task 35", "Task 36")

        taskList.add(TasksModel(1, "Forklift", "In Progress", 1, machine1Tasks))
        taskList.add(TasksModel(2, "Excavator", "Pending", 2, machine2Tasks))
        taskList.add(TasksModel(3, "Compactor", "Pending", 2, machine3Tasks))
        taskList.add(TasksModel(4, "Dump-Truck", "Pending", 2, machine4Tasks))
        taskList.add(TasksModel(5, "Flat-Bed-Truck", "Pending", 2, machine5Tasks))
        taskList.add(TasksModel(6, "Transit-Mixer", "Pending", 3, machine6Tasks))
        taskList.add(TasksModel(7, "Grader", "Pending", 2, machine7Tasks))
        taskList.add(TasksModel(8, "Bulldozer", "Pending", 3, machine8Tasks))
        taskList.add(TasksModel(9, "Crane", "Pending", 2, machine9Tasks))
        taskList.add(TasksModel(10, "Road-Roller", "Pending", 3, machine10Tasks))
        taskList.add(TasksModel(11, "Loader", "Pending", 2, machine11Tasks))
        taskList.add(TasksModel(12, "Backhoe", "Pending", 3, machine12Tasks))
        taskList.add(TasksModel(13, "One-Bagger", "Pending", 3, machine13Tasks))

        // Notify adapter about data changes
        myAdapter.notifyDataSetChanged()
    }
}
