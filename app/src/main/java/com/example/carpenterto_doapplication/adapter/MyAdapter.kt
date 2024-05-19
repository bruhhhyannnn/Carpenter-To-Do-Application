package com.example.carpenterto_doapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.dashboard.MachineDetailActivity
import com.example.carpenterto_doapplication.data_model.TasksModel

class MyAdapter(private val machineList: List<TasksModel>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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
        holder.progressNumber.text = "${machine.progressNumber}%"

        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MachineDetailActivity::class.java).apply {
                putExtra("machineName", machine.machineName)
                putExtra("progressState", machine.progressState)
                putExtra("progressNumber", machine.progressNumber)
                putStringArrayListExtra("tasks", ArrayList(machine.tasks))
            }
            context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.machine_name)
        val progressState: TextView = itemView.findViewById(R.id.progress_state)
        val progressNumber: TextView = itemView.findViewById(R.id.progress_number)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
