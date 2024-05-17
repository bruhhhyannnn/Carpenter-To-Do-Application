package com.example.carpenterto_doapplication.splash_art

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.authentication.RegistrationActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, RegistrationActivity::class.java))
            finish()
        }, 1008)

    }
}