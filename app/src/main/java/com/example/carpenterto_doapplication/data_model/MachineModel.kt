package com.example.carpenterto_doapplication.data_model

data class MachineModel(
    var machineId: Int,
    var machineName: String = "",
    var progressState: String = "",
    var progressNumber: Int,
    var tasks: MutableList<String> = mutableListOf(),
    var tasksCompleted: MutableList<Boolean> = MutableList(tasks.size) { false }
)
