package com.example.carpenterto_doapplication.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.data_model.ReportModel

class ReportAdapter (
    private val reportList: ArrayList<ReportModel>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.reportTitle.text = report.reportTitle
        holder.reportDateGenerated.text = report.reportDate
        holder.reportTimeGenerated.text = report.reportTime
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportTitle: TextView = itemView.findViewById(R.id.report_title)
        val reportDateGenerated: TextView = itemView.findViewById(R.id.report_date_generated)
        val reportTimeGenerated: TextView = itemView.findViewById(R.id.report_time_generated)

//        init {
//            itemView.setOnClickListener {view: View ->
//                val position = adapterPosition
//                Toast.makeText(itemView.context, "Item $position clicked", Toast.LENGTH_SHORT).show()
//                if (position != RecyclerView.NO_POSITION) {
//                    val report = reportList[position]
//                    // Handle click on the report item
//                    // For example, show a dialog with the report details
//                    // ...
//                    // Show a dialog with the report details
////                    val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.fragment_report, null)
////                    val dialog = AlertDialog.Builder(itemView.context)
////                        .setView(dialogView)
////                        .create()
////                    dialog.show()
//                }
//
//            }
//        }
    }
}