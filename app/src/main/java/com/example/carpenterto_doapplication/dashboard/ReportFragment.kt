package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ReportAdapter
import com.example.carpenterto_doapplication.data_model.ReportModel
import java.util.UUID

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportData: ArrayList<ReportModel>
    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        getData()

        recyclerView = view.findViewById(R.id.report_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    private fun getData() {
        reportData = ArrayList()
        reportData.add(ReportModel(UUID.randomUUID().toString(), "Forklift", "06/01/2024", "5:00 PM"))

        reportAdapter = ReportAdapter(reportData)
        recyclerView.adapter = reportAdapter

    }

}