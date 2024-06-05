package com.example.carpenterto_doapplication.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.MachineModel

class MachineAdapter(
    private val machineList: ArrayList<MachineModel>
) : RecyclerView.Adapter<MachineAdapter.MachineViewHolder>() {

    var onItemClick: ((MachineModel) -> Unit)? = null

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

        holder.horizontalProgressBar.max = 1000
        animateHorizontalProgressBar(holder.horizontalProgressBar, machine.progressNumber)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(machine)
        }
    }

    private fun animateHorizontalProgressBar(progressBar: ProgressBar, progress: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress * 10)
            .setDuration(2000)
            .start()
    }

    class MachineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineName: TextView = itemView.findViewById(R.id.machine_name)
        val progressState: TextView = itemView.findViewById(R.id.progress_state)
        val progressNumber: TextView = itemView.findViewById(R.id.progress_number)
        val horizontalProgressBar: ProgressBar = itemView.findViewById(R.id.horizontal_progress_bar)
    }
}
