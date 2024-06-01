package com.example.carpenterto_doapplication.data_model

import java.util.UUID

data class ReportModel(
    var reportId: String = UUID.randomUUID().toString(),
    var reportTitle: String = "",
    var reportDate : String = "",
    var reportTime : String = ""
)
