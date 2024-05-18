package com.example.carpenterto_doapplication.user_data_model

data class UserModel(
    var id : String = "",
    var email : String = "",
    var username : String = "",
    var profilePicture : String = "",
    var reports : MutableList<String> = mutableListOf()
)
