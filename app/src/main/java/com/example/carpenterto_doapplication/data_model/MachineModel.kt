package com.example.carpenterto_doapplication.data_model

data class MachineModel(
    var userId : String = "",
    var machineId: List<Int> = List(13) { it + 1 },
    var machineName: String = "",
    var progressState: String = "",
    var progressNumber: Int
)
