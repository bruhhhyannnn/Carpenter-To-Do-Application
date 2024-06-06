package com.example.carpenterto_doapplication.splash_art

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.carpenterto_doapplication.MainActivity
import com.example.carpenterto_doapplication.databinding.ActivitySplashWelcomeBinding
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashWelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashWelcomeBinding
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid

    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            getName()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getName()
                } else {
                    UiUtil.showToast(this, "Write permission is required for this Application")
                    finish() // Close the app if permission is not granted
                }
            }
        }
    }

    private fun getName() {
        Firebase.firestore.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    binding.nameText.text = documents.documents[0].data?.get("fullName").toString().substringBefore(" ")
                } else {
                    logout()
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

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
