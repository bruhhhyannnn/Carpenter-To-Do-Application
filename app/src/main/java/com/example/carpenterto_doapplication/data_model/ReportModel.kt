package com.example.carpenterto_doapplication.data_model

data class ReportModel(
    var fullName: String = "",
    var reportId: String = "",
    var machineName: String = "",
    var reportDate: String = "",
    var reportTime: String = "",
    var progressNumber: Double = 0.0,
    var progressState: String = ""
)
