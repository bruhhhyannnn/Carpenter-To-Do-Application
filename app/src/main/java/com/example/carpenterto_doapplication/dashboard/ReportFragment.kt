package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.adapter.ReportAdapter
import com.example.carpenterto_doapplication.data_model.ReportModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.UUID

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportData: ArrayList<ReportModel>
    private lateinit var reportAdapter: ReportAdapter

    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var reportsFound : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        reportsFound = view.findViewById(R.id.reports_found)
        recyclerView = view.findViewById(R.id.report_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        reportData = ArrayList()
        reportAdapter = ReportAdapter(reportData)
        recyclerView.adapter = reportAdapter

        getReportDataFromFirebase()

        return view
    }

    private fun getReportDataFromFirebase() {
        Firebase.firestore
            .collection("reports")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    reportsFound.visibility = View.VISIBLE
                } else {
                    reportsFound.visibility = View.GONE
                    for (document in documents) {
                        val reportModel = document.toObject(ReportModel::class.java)
                        reportModel.let {
                            reportData.add(it)
                        }
                        reportAdapter.notifyDataSetChanged()
                    }

                }
            }
            .addOnFailureListener { exception ->
                UiUtil.showToast(requireContext(), "Failed to fetch data from Firebase: ${exception.message}")
            }
    }

//    private fun deleteReport(position: Int) {
//        reportData.removeAt(position)
//        reportAdapter.notifyDataSetChanged()
//
//    }

}