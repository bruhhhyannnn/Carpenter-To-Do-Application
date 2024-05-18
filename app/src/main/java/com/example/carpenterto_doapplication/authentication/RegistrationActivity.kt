package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivityRegistrationBinding
import com.example.carpenterto_doapplication.splash_art.SplashWelcomeActivity
import com.example.carpenterto_doapplication.user_data_model.UserModel
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountButton.setOnClickListener {
            createAccount()

        }

        binding.loginTextButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    fun setInProgress(inProgress : Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.createAccountButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.createAccountButton.visibility = View.VISIBLE
        }
    }

    private fun createAccount() {
        val email = binding.emailAddressText.text.toString()
        val username = binding.usernameText.text.toString()
        val password = binding.passwordText.text.toString()
        val confirmPassword = binding.confirmPasswordText.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAddressText.setError("Email is invalid")
            return
        }
        if (password.length < 8) {
            binding.passwordText.setError("Minimum of 8 characters")
            return
        }
        if (password != confirmPassword) {
            binding.confirmPasswordText.setError("Password does not matched")
            return
        }

        createAccountWithFirebase(email, username, password)
    }

    private fun createAccountWithFirebase(email: String, username: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let {user ->
                    val userModel = UserModel(user.uid, email, username)
                    Firebase.firestore
                        .collection("users")
                        .document(user.uid)
                        .set(userModel)
                        .addOnSuccessListener {
                            UiUtil.showToast(applicationContext, "Account created successfully")
                            setInProgress(false)
                            startActivity(Intent(this, SplashWelcomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            UiUtil.showToast(applicationContext, "Seriously?")
                            setInProgress(false)
                        }
                }
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
                setInProgress(false)
            }
    }
}