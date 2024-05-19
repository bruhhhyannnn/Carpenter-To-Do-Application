package com.example.carpenterto_doapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.carpenterto_doapplication.dashboard.ReportsFragment
import com.example.carpenterto_doapplication.dashboard.TasksFragment
import com.example.carpenterto_doapplication.databinding.ActivityMainBinding
import com.example.carpenterto_doapplication.settings.SettingsActivity
import com.example.carpenterto_doapplication.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.DateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Firebase.firestore
//            .collection("users")
//            .whereEqualTo("id", FirebaseAuth.getInstance().currentUser!!.uid)
//            .get()
//            .addOnSuccessListener {
//                postcount.text = it.size().toString()
//            }





        replaceFragment(TasksFragment())
        bindHeader()

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.bottom_menu_tasks -> {
                    replaceFragment(TasksFragment())
                }
                R.id.bottom_menu_reports -> {
                    replaceFragment(ReportsFragment())
                }

            }
            false
        }
    }

    private fun bindHeader() {
        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        Firebase.firestore
            .collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                binding.firstNameText.text = it.documents[0].data?.get("fullName").toString().substringBefore(" ")
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            }
        binding.dateTodayText.text = "Today: " + dateFormat
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}