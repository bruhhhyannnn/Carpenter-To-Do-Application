package com.example.carpenterto_doapplication.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.dashboard.MachineDetailActivity
import com.example.carpenterto_doapplication.data_model.TasksModel

class MyAdapter constructor(
    private val activity: Activity,
    private val machineList: List<TasksModel>
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_machine_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return machineList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val machine = machineList[position]
        holder.machineName.text = machine.machineName
        holder.progressState.text = machine.progressState
        holder.progressNumber.text = machine.progressNumber.toString()

        holder.cardView.setOnClickListener {
            val intent = Intent(activity, MachineDetailActivity::class.java).apply {
                putExtra("machineId", machine.machineId)
                putExtra("machineName", machine.machineName)
                putExtra("progressState", machine.progressState)
                putExtra("progressNumber", machine.progressNumber)
                putStringArrayListExtra("tasks", ArrayList(machine.tasks))
                putExtra("tasksCompleted", machine.tasksCompleted.toBooleanArray())
            }
            activity.startActivity(intent)
            Toast.makeText(activity, machine.machineName, Toast.LENGTH_SHORT).show()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.machine_name)
        val progressState: TextView = itemView.findViewById(R.id.progress_state)
        val progressNumber: TextView = itemView.findViewById(R.id.progress_number)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
