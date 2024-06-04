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
                    Log.d("Firestore", "Added $machine to userMachines!")
                }
                .addOnFailureListener { e ->
                    UiUtil.showToast(applicationContext, e.localizedMessage ?: "Something went wrong")
                }
        }

        addMaintenanceTasksToFirebase()
    }

    private fun addMaintenanceTasksToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val machines = resources.getStringArray(R.array.machineList)
        val dailyMaintenance = resources.getStringArray(R.array.daily_maintenance).toList()
        val monthlyMaintenance = resources.getStringArray(R.array.monthly_maintenance).toList()
        val asNeededMaintenance = resources.getStringArray(R.array.as_needed_maintenance).toList()

        fun addMaintenanceData(collectionName: String, tasks: List<String>) {
            val userMachinesRef = Firebase.firestore
                .collection("tasks")
                .document(userId)
                .collection(collectionName)

            for (machine in machines) {
                val maintenanceData = hashMapOf(
                    "tasks" to tasks,
                    "tasksCompleted" to MutableList(tasks.size) { false }
                )

                userMachinesRef.document(machine)
                    .set(maintenanceData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "$collectionName for machine $machine successfully written!")
                    }
                    .addOnFailureListener { e ->
                        UiUtil.showToast(applicationContext, e.localizedMessage ?: "Something went wrong")
                    }
            }
        }

        addMaintenanceData("dailyMaintenance", dailyMaintenance)
        addMaintenanceData("monthlyMaintenance", monthlyMaintenance)
        addMaintenanceData("asNeededMaintenance", asNeededMaintenance)

        // Suggested Maintenance Data
        val suggestedMaintenanceData = mapOf(
            "Loader" to listOf(
                "Verify the machine operator's level of training",
                "Examine the attachment cutting edges visually.",
                "Understand the tread on your tires and how to properly inflate them.",
                "Uphold the parking and driving brake",
                "Maintain clean axles and driveline seals"
            ),
            "Excavator" to listOf(
                "Verify the machine operator's level of training",
                "Examine the attachment cutting edges visually.",
                "Understand the tread on your tires and how to properly inflate them.",
                "Uphold the parking and driving brake",
                "Maintain clean axles and driveline seals"
            ),
            "Backhoe" to listOf(
                "Clean the hydraulic system and engine compartment, taking care of any leaks or problems.",
                "Make sure the undercarriage is completely clean to avoid accumulation and to guard against corrosion",
                "Lubricate the lubrication points using grease",
                "Keep the backhoe loader out of direct sunlight and in a dry, covered place",
                "For long-term storage, think about unplugging the battery or employing a battery maintainer. While storing, keep an eye out for any damage, leaks, or bug infestation"
            ),
            "Crane" to listOf(
                "Ensure proper alignment.",
                "Inspect chains and connection for damages.",
                "Make sure the hook is intact",
                "Check air and hydraulic system."
            ),
            "Road-Roller" to listOf(
                "Keep the drum clean and lubricate",
                "Monitor drum scrap",
                "Inspect shock mounts."
            ),
            "Bulldozer" to listOf(
                "Check the track tension",
                "Inspect the carrier roller and the idler.",
                "Inspect track pads.",
                "Clean undercarriage."
            ),
            "Grader" to listOf(
                "Inspect the proper voltage",
                "Inspect the moldboard and linkage",
                "Lubricate moving parts",
                "Check and adjust belt",
                "Clean and adequately store motor graders"
            ),
            "Transit-Mixer" to listOf(
                "Use a concrete dissolver to clean your truck",
                "Rinse off chute",
                "Attend concrete mixer classes",
                "Consider wash out system",
                "Inspection process overhaul"
            ),
            "Flat-Bed-Truck" to listOf(
                "Clean Exterior",
                "Inspect the truck",
                "Check alignments",
                "Check steering fluids",
                "Check hardware “suspension and steering”"
            ),
            "Dump-Truck" to listOf(
                "Make sure operator is knowledgeable",
                "Make sure to warm up before using.",
                "Avoid excessive idling"
            ),
            "Compactor" to listOf(
                "Check that all safety guards and access covers are secure and in proper condition.",
                "Have a licensed electrician inspect the electrical system. All electrical connections should be examined and the motor amp draw should be checked."
            ),
            "Forklift" to listOf(
                "Thoroughly inspect for any cracks appearing in the forklift structural elements",
                "Lubricate the chassis and mast components.",
                "Adjust the engine timing and idle if necessary.",
                "Inspect the drive belt tension.",
                "Inspect the lift, tilt and cylinder operations."
            )
        )

        fun addSuggestedMaintenance(machine: String, tasks: List<String>) {
            val userTasksRef = Firebase.firestore
                .collection("tasks")
                .document(userId)
                .collection("suggestedMaintenance")
                .document(machine)

            val maintenanceData = hashMapOf(
                "tasks" to tasks,
                "tasksCompleted" to MutableList(tasks.size) { false }
            )

            userTasksRef.set(maintenanceData)
                .addOnSuccessListener {
                    Log.d("Firestore", "suggestedMaintenance for $machine successfully written!")
                }
                .addOnFailureListener { e ->
                    UiUtil.showToast(applicationContext, e.localizedMessage ?: "Something went wrong")
                }
        }

        for ((machine, tasks) in suggestedMaintenanceData) {
            addSuggestedMaintenance(machine, tasks)
        }
    }

}