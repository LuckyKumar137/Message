package com.example.message

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log

class CreateContact : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputname = findViewById<EditText>(R.id.lkname)
        val inputnumber = findViewById<EditText>(R.id.lknumber)
        val save = findViewById<Button>(R.id.save)

        save.setOnClickListener {
            val name = inputname.text.toString().trim()
            val number = inputnumber.text.toString().trim()

            // Check if both fields are filled
            if (name.isBlank() || number.isBlank()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("CreateContact", "Name: $name, Number: $number")  // Log data being passed

                val intent = Intent()
                intent.putExtra("name", name)
                intent.putExtra("number", number)

                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()

                setResult(RESULT_OK, intent)
                finish()
            }
        }

        val bk = findViewById<ImageView>(R.id.back)
        bk.setOnClickListener {
            finish()
        }
    }
}
