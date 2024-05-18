package com.example.carpenterto_doapplication.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carpenterto_doapplication.databinding.ActivityMaintenanceBinding

class MaintenanceActivity : AppCompatActivity() {

    lateinit var binding: ActivityMaintenanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}