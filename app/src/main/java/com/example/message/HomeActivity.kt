package com.example.message

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {

    lateinit var recyler: RecyclerView
    lateinit var myAdapter: MyAdapter
    private val userList = mutableListOf<MyData>()

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("user_data", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Set up UI and RecyclerView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyler = findViewById(R.id.recylerlk)
        recyler.layoutManager = LinearLayoutManager(this)
        myAdapter = MyAdapter(this, userList)
        recyler.adapter = myAdapter

        // Load saved user data from SharedPreferences
        loadUserList()

        // Profile button click listener
        val account = findViewById<ImageView>(R.id.Man)
        account.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        // Plus icon click listener to add new user
        val plusicon = findViewById<ImageView>(R.id.plus)
        plusicon.setOnClickListener {
            val intent = Intent(this, CreateContact::class.java)
            startActivityForResult(intent, 1)
        }
    }

    // Handling result from CreateContact activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val name = data.getStringExtra("name") ?: ""
            val number = data.getStringExtra("number") ?: ""

            // Add the new contact to the list
            val contact = MyData(name, number)
            userList.add(contact)

            // Save the updated list to SharedPreferences
            saveUserList()

            // Notify adapter about the change
            myAdapter.notifyItemInserted(userList.size - 1)
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Save the user list to SharedPreferences
    private fun saveUserList() {
        val gson = Gson()
        val json = gson.toJson(userList)
        val editor = sharedPreferences.edit()
        editor.putString("user_list", json)
        editor.apply()
    }

    // Load the user list from SharedPreferences
    private fun loadUserList() {
        val gson = Gson()
        val json = sharedPreferences.getString("user_list", null)
        if (json != null) {
            val type = object : TypeToken<List<MyData>>() {}.type
            val savedUserList: List<MyData> = gson.fromJson(json, type)
            userList.clear()
            userList.addAll(savedUserList)
        }
    }
}
