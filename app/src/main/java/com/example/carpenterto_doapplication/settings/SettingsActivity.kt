package com.example.carpenterto_doapplication.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivitySettingsBinding
import com.example.carpenterto_doapplication.splash_art.SplashActivity
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
            startActivity(Intent(this, SettingsProfileActivity::class.java))
        }

        binding.aboutButton.setOnClickListener {
            showAboutAlertDialog()
        }

        binding.changePasswordButton.setOnClickListener {
            startActivity(Intent(this, SettingsChangePasswordActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            showLogoutAlertDialog()
        }


    }

    private fun showAboutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About this application")
        builder.setMessage("This is a system to ease out the mundane tasks that is done in day-to-day life of a very hard working person.")
        builder.setPositiveButton("Okay!") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showLogoutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("Please make sure you have completed all your tasks.")
        builder.setPositiveButton("Log Out") { dialog, _ ->
            logout()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun bindHeader() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("users")
            .whereEqualTo("userId", userId)
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