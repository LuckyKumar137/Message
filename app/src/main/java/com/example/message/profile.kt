package com.example.message

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back = findViewById<ImageView>(R.id.end)


        val nameText = findViewById<TextView>(R.id.profilename)
        val numberText = findViewById<TextView>(R.id.profilenumber)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val name = sharedPref.getString("userName", "No Name")
        val number = sharedPref.getString("userNumber", "No Number")

        nameText.text = "Name : $name"
        numberText.text = "Number : $number"

        back.setOnClickListener {
            finish()
        }

    }
}