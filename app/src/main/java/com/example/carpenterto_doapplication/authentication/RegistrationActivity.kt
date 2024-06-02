package com.example.carpenterto_doapplication.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.R
import com.example.carpenterto_doapplication.databinding.ActivityRegistrationBinding
import com.example.carpenterto_doapplication.splash_art.SplashWelcomeActivity
import com.example.carpenterto_doapplication.data_model.UserModel
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
        val fullName = binding.fullNameText.text.toString()
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

        createAccountWithFirebase(email, fullName, password)
    }

    private fun createAccountWithFirebase(email: String, fullName: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let {user ->
                    val userModel = UserModel(user.uid, email, fullName)
                    Firebase.firestore
                        .collection("users")
                        .document(user.uid)
                        .set(userModel)
                        .addOnSuccessListener {
                            UiUtil.showToast(applicationContext, "Account created successfully")
                            addMachineDataToFirebase()
                            setInProgress(false)
                            startActivity(Intent(this, SplashWelcomeActivity::class.java))
                            finish()
                        }
                }
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
                setInProgress(false)
            }
    }

    private fun addMachineDataToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val machines = resources.getStringArray(R.array.machineList)
        val userMachinesRef = Firebase.firestore
            .collection("machines")
            .document(userId)
            .collection("userMachines")

        for ((index, machine) in machines.withIndex()) {
            val machineData = hashMapOf(
                "machineId" to (index + 1),
                "machineName" to machine,
                "progressState" to "Not Started",
                "progressNumber" to 0,
            )
            userMachinesRef.add(machineData)
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully written for $machine!")
                }
                .addOnFailureListener { e ->
                    UiUtil.showToast(applicationContext, e.localizedMessage ?: "Something went wrong")
                }
        }

        addMaintenanceTasksToFirebase()
    }

    private fun addMaintenanceTasksToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dailyMaintenance = resources.getStringArray(R.array.daily_maintenance).toList()
        val monthlyMaintenance = resources.getStringArray(R.array.monthly_maintenance).toList()
        val asNeededMaintenance = resources.getStringArray(R.array.as_needed_maintenance).toList()

        fun addMaintenanceData(collectionName: String, taskNames: List<String>) {
            val userMachinesRef = Firebase.firestore
                .collection("tasks")
                .document(userId)
                .collection(collectionName)

            val maintenanceData = hashMapOf(
                "tasksCompleted" to MutableList(taskNames.size) { false },
                "tasksCompleteName" to taskNames
            )

            userMachinesRef.add(maintenanceData)
                .addOnSuccessListener {
                    Log.d("Firestore", "$collectionName DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    UiUtil.showToast(applicationContext, e.localizedMessage ?: "Something went wrong")
                }
        }

        addMaintenanceData("dailyMaintenance", dailyMaintenance)
        addMaintenanceData("monthlyMaintenance", monthlyMaintenance)
        addMaintenanceData("asNeededMaintenance", asNeededMaintenance)
    }
}