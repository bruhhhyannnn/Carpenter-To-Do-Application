package com.example.carpenterto_doapplication.data_model

data class TasksCompletedModel(
    val maintenanceType : String = "",
    val arrowImage: Int = 0,
    val tasksCompleted: List<String> = emptyList()
)
