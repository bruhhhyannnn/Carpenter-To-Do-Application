package com.example.carpenterto_doapplication.data_model

data class MaintenanceTypesModel(
    val maintenanceType: String = "",
    val arrowImage: Int = 0,
    val childItemList: ArrayList<TaskModel>,
    var isExpanded: Boolean = false
)
