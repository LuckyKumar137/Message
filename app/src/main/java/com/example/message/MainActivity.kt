package com.example.message

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        // ✅ Always use the SAME name
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isSignedUp = sharedPref.getBoolean("isSignedUp", false)

        if (isSignedUp) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val editfirst = findViewById<EditText>(R.id.editTname)
        val editsecond = findViewById<EditText>(R.id.editTnumber)
        val save = findViewById<Button>(R.id.signup)

        save.setOnClickListener {
            val name = editfirst.text.toString()
            val number = editsecond.text.toString()

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = Data(name, number)
            database = FirebaseDatabase.getInstance().getReference("Users")
            database.child(number).setValue(users).addOnSuccessListener {

                // ✅ Save sign-up state here too
                sharedPref.edit().apply {
                    putString("userName", name)
                    putString("userNumber", number)
                    putBoolean("isSignedUp", true)
                    apply()
                }

                Toast.makeText(applicationContext, "User Registered", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()

            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
