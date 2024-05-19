package com.example.carpenterto_doapplication.data_model

data class TasksModel(
    var machineId: Int,
    var machine: String = "",
    var tasks: MutableList<String> = mutableListOf(),
    var completion: Int
)
