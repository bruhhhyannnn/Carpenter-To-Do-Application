package com.example.carpenterto_doapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.MainActivity
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.MachineModel
import com.example.carpenterto_doapplication.util.UiUtil

class MachineAdapter(
    private val machineList:ArrayList<MachineModel>)
    : RecyclerView.Adapter<MachineAdapter.MachineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_machine, parent, false)
        return MachineViewHolder(view)
    }

    override fun getItemCount(): Int {
        return machineList.size
    }

    override fun onBindViewHolder(holder: MachineViewHolder, position: Int) {
        val machine = machineList[position]
        holder.machineName.text = machine.machineName
        holder.progressState.text = machine.progressState
        holder.progressNumber.text = machine.progressNumber.toString()

        holder.cardView.setOnClickListener {
//            UiUtil.showToast(getActivity, machineList[position].machineName)
        }
    }


    class MachineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.machine_name)
        val progressState: TextView = itemView.findViewById(R.id.progress_state)
        val progressNumber: TextView = itemView.findViewById(R.id.progress_number)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

    }
}
