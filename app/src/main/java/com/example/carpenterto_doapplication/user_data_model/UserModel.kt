package com.example.carpenterto_doapplication.user_data_model

data class UserModel(
    var id : String = "",
    var email : String = "",
    var fullName : String = "",
    var profilePicture : String = "",
    var reports : MutableList<String> = mutableListOf()
)
