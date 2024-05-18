package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivityLoginBinding
import com.example.carpenterto_doapplication.splash_art.SplashWelcomeActivity

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, SplashWelcomeActivity::class.java))
            finish()
        }

        binding.signupTextButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }
}