package com.example.carpenterto_doapplication.splash_art

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.MainActivity
import com.example.carpenterto_doapplication.databinding.ActivitySplashWelcomeBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SplashWelcomeActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getName()
    }

    fun getName() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                binding.nameText.text = it.documents[0].data?.get("username").toString()
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            }

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
}