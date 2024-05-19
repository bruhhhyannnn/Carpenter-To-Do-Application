package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.databinding.ActivityForgotPasswordBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetPasswordButton.setOnClickListener {
            resetPassword()
        }

        binding.loginTextButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.resetPasswordButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.resetPasswordButton.visibility = View.VISIBLE
        }
    }

    fun resetPassword() {
        val email = binding.emailAddressText.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAddressText.setError("Email not valid")
            return
        }

        resetPasswordWithFirebase(email)
    }

    fun resetPasswordWithFirebase(email : String) {
        setInProgress(true)

        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                UiUtil.showToast(applicationContext, "Reset password link sent to your Email")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                binding.emailAddressText.setError("Email not valid")
                setInProgress(false)
            }

    }
}