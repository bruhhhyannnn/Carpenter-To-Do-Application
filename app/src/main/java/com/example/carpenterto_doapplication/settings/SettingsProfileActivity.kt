package com.example.carpenterto_doapplication.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivitySettingsProfileBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.DateFormat
import java.util.Calendar

class SettingsProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindHeaderAndMain()
    }

    private fun bindHeaderAndMain() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                binding.firstNameText.text = it.documents[0].data?.get("fullName").toString().substringBefore(" ")
                binding.fullName.text = it.documents[0].data?.get("fullName").toString()
                binding.emailAddress.text = FirebaseAuth.getInstance().currentUser?.email
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            }

        binding.dateTodayText.text = "Today: " + dateFormat
    }
}