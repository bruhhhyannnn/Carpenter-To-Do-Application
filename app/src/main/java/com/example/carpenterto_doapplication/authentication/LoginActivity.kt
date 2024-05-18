package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivityLoginBinding
import com.example.carpenterto_doapplication.splash_art.SplashWelcomeActivity
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuth.getInstance().currentUser?.let {
            startActivity(Intent(this, SplashWelcomeActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.signupTextButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }

    fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.loginButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.loginButton.visibility = View.VISIBLE
        }
    }

    fun login() {
        val email = binding.emailAddressText.text.toString()
        val password = binding.passwordText.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAddressText.setError("Email is invalid")
            return
        }
        if (password.length < 8) {
            binding.passwordText.setError("Minimum of 8 characters")
            return
        }

        loginWithFirebase(email, password)
    }

    fun loginWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                UiUtil.showToast(applicationContext, "Login successfully")
                setInProgress(false)
                startActivity(Intent(this, SplashWelcomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                binding.passwordText.setError("Wrong credentials")
                setInProgress(false)
            }
    }
}