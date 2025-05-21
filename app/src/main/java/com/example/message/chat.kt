package com.example.message

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class chat : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private lateinit var senderNumber: String
    private lateinit var receiverNumber: String

    private lateinit var sharedPref: SharedPreferences
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userNameTextView = findViewById<TextView>(R.id.usertext)
        val userNumberTextView = findViewById<TextView>(R.id.usernumber)
        val backButton = findViewById<ImageView>(R.id.imageViewbk)
        messageInput = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.sendButton)
        recyclerView = findViewById(R.id.recyaclermesage)

        val getUserName = intent.getStringExtra("NAME")
        val getUserNumber = intent.getStringExtra("NUMBER")

        if (getUserName == null || getUserNumber == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userNameTextView.text = getUserName
        userNumberTextView.text = getUserNumber

        backButton.setOnClickListener { finish() }

        sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        gson = Gson()

        senderNumber = sharedPref.getString("userNumber", "") ?: ""
        receiverNumber = getUserNumber

        if (senderNumber.isEmpty()) {
            Toast.makeText(this, "User not logged in properly", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = MessageAdapter(this, messages, senderNumber)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView.post {
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        messages.clear()
        adapter.notifyDataSetChanged()
        loadMessages()

        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("messages/$senderNumber/$receiverNumber")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    if (!messages.any { m -> m.text == it.text && m.timestamp == it.timestamp && m.senderId == it.senderId }) {
                        messages.add(it)
                        adapter.notifyItemInserted(messages.size - 1)
                        recyclerView.scrollToPosition(messages.size - 1)
                        saveMessages()
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()
                val message = Message(
                    text = text,
                    isSent = true,
                    senderId = senderNumber,
                    receiverId = receiverNumber,
                    timestamp = timestamp
                )

                val messageRef = database.getReference("messages/$senderNumber/$receiverNumber")
                val messageId = messageRef.push().key

                // ✅ Immediately clear input box
                messageInput.setText("")

                if (messageId != null) {
                    messageRef.child(messageId).setValue(message)
                }

                if (!messages.any { m -> m.text == message.text && m.timestamp == message.timestamp }) {
                    messages.add(message)
                    adapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                    saveMessages()
                }

                // Send to receiver's node
                val reverseRef = database.getReference("messages/$receiverNumber/$senderNumber")
                val reverseMessage = message.copy(isSent = false)
                val reverseMessageId = reverseRef.push().key
                if (reverseMessageId != null) {
                    reverseRef.child(reverseMessageId).setValue(reverseMessage)
                }
            }
        }
    }

    fun saveMessages() {
        val editor = sharedPref.edit()
        val json = gson.toJson(messages)
        val key = "messages_${senderNumber}_$receiverNumber"
        editor.putString(key, json)
        editor.apply()
    }

    private fun loadMessages() {
        val key = "messages_${senderNumber}_$receiverNumber"
        val json = sharedPref.getString(key, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Message>>() {}.type
            val savedMessages: MutableList<Message> = gson.fromJson(json, type)
            messages.addAll(savedMessages)
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }
}
