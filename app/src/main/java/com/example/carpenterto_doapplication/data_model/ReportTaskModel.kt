package com.example.carpenterto_doapplication.data_model

data class ReportTaskModel(
    var maintenanceType: String = "",
    var tasksCompleted: List<String> = emptyList()
)
