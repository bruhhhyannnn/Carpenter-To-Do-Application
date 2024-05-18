package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivityRegistrationBinding
import com.example.carpenterto_doapplication.splash_art.SplashWelcomeActivity

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountButton.setOnClickListener {
            startActivity(Intent(this, SplashWelcomeActivity::class.java))
            finish()
        }

        binding.loginTextButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}