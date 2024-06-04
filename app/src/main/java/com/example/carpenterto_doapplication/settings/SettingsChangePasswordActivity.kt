package com.example.carpenterto_doapplication.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivitySettingsChangePasswordBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.DateFormat
import java.util.Calendar

class SettingsChangePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsChangePasswordBinding

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

    fun validate() {
        val current_password = binding.currentPassword.text.toString()
        val new_password = binding.newPassword.text.toString()
        val confirm_password = binding.confirmPassword.text.toString()

        if (current_password.isEmpty()) {
            binding.currentPassword.setError("Wrong password")
            return
        }
        if (new_password.isEmpty() || new_password.length < 8) {
            binding.newPassword.setError("Enter proper new password")
            return
        }
        if (confirm_password.isEmpty() || confirm_password.length < 8) {
            binding.confirmPassword.setError("Password doesn't match")
            return
        }
        updatePasswordWithFirebase()
    }

    fun updatePasswordWithFirebase() {
        // TODO: Update password in firebase
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