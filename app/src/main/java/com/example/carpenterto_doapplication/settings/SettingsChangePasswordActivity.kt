package com.example.carpenterto_doapplication.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivitySettingsChangePasswordBinding
import com.example.carpenterto_doapplication.splash_art.SplashActivity
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.DateFormat
import java.util.Calendar

class SettingsChangePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsChangePasswordBinding

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindHeader()

        binding.saveButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure?")
            builder.setMessage("Please make sure you memorize your passwords.")
            builder.setPositiveButton("Change Password") { dialog, _ ->
                validate()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
        }
    }

    fun validate() {
        val currentPassword = binding.currentPassword.text.toString()
        val newPassword = binding.newPassword.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        if (currentPassword.isEmpty() || currentPassword.length < 8) {
            binding.currentPassword.error = "Enter a valid current password"
            return
        }
        if (newPassword.isEmpty() || newPassword.length < 8) {
            binding.newPassword.error = "Enter a valid new password"
            return
        }
        if (confirmPassword.isEmpty() || confirmPassword.length < 8) {
            binding.confirmPassword.error = "Confirm your password"
            return
        }
        if (newPassword != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match"
            return
        }

        updatePasswordWithFirebase(currentPassword, newPassword)
    }

    private fun updatePasswordWithFirebase(currentPassword : String, newPassword : String) {
        setInProgress(true)
        val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        UiUtil.showToast(applicationContext, "Password changed successfully")
                        setInProgress(false)
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, SplashActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        UiUtil.showToast(applicationContext, it.localizedMessage ?: "Something went wrong")
                        setInProgress(false)
                    }
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage ?: "Something went wrong")
                setInProgress(false)
            }
    }

    private fun bindHeader() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)

        Firebase.firestore
            .collection("users")
            .whereEqualTo("userId", user?.uid!!)
            .get()
            .addOnSuccessListener {
                binding.firstNameText.text = it.documents[0].data?.get("fullName").toString().substringBefore(" ")
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            }

        binding.dateTodayText.text = "Today: $dateFormat"
    }
}