package com.example.carpenterto_doapplication.splash_art

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.MainActivity
import com.example.carpenterto_doapplication.databinding.ActivitySplashWelcomeBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashWelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashWelcomeBinding
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getName()
    }

    private fun getName() {
        Firebase.firestore.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    binding.nameText.text = documents.documents[0].data?.get("fullName").toString().substringBefore(" ")
                } else {
                    UiUtil.showToast(applicationContext, "User document not found")
                }
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage ?: "Something went wrong")
            }

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
