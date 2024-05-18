package com.example.carpenterto_doapplication.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.dashboard.ReportsFragment
import com.example.carpenterto_doapplication.dashboard.TasksFragment
import com.example.carpenterto_doapplication.databinding.ActivitySettingsBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.DateFormat
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindHeader()

        binding.accountButton.setOnClickListener {

        }

        binding.aboutButton.setOnClickListener {

        }

        binding.logoutButton.setOnClickListener {

        }


    }

    private fun bindHeader() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                binding.firstNameText.text = it.documents[0].data?.get("fullName").toString().substringBefore(" ")
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            }

        binding.dateTodayText.text = "Today: " + dateFormat
    }
}