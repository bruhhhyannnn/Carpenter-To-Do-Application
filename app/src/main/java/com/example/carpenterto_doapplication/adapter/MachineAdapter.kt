package com.example.carpenterto_doapplication.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.MachineModel

class MachineAdapter(
    private val activity: Activity,
    private val machineList: List<MachineModel>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MachineAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_machine, parent, false)
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
            onItemClickListener.onItemClick(position)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.machine_name)
        val progressState: TextView = itemView.findViewById(R.id.progress_state)
        val progressNumber: TextView = itemView.findViewById(R.id.progress_number)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
