package com.example.carpenterto_doapplication.data_model

data class TaskModel(
    var userId : String = "",
    var machineId : Int,
    var taskId : Int,
    var taskName : String = "",
//    var isCompleted : Boolean
)
