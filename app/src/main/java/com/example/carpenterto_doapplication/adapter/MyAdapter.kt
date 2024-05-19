package com.example.carpenterto_doapplication.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.data_model.TasksModel

class MyAdapter(private val machineList : ArrayList<TasksModel>) : RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return machineList.size
    }

    class MyViewHoder(itemView : View) : RecyclerView.ViewHolder(itemView) {


        val machineName : TextView = itemView.findViewById(R.id.machineName)



    }
}