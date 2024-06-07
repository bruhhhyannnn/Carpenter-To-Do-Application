package com.example.carpenterto_doapplication.data_model

data class ReportModel(
    var reportId: String = "",
    var machineName: String = "",
    var reportDate: String = "",
    var reportTime: String = "",
    var dailyTasks: ArrayList<ReportTaskModel> = ArrayList(),
    var monthlyTasks: ArrayList<ReportTaskModel> = ArrayList(),
    var asNeededTasks: ArrayList<ReportTaskModel> = ArrayList(),
    var suggestedTasks: ArrayList<ReportTaskModel> = ArrayList()
)
