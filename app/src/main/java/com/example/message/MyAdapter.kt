package com.example.message

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyAdapter(val context: Activity, val userList: MutableList<MyData>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("user_data", Activity.MODE_PRIVATE)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NAME: TextView = itemView.findViewById(R.id.Ename)
        val NUMBER: TextView = itemView.findViewById(R.id.Enumber)
        val cd: CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = userList[position]
        holder.NAME.text = currentData.Username
        holder.NUMBER.text = currentData.UserNumber

        // Open chat activity when clicking on the user
        holder.cd.setOnClickListener {
            val intent = Intent(holder.itemView.context, chat::class.java)
            intent.putExtra("NAME", currentData.Username)
            intent.putExtra("NUMBER", currentData.UserNumber)
            holder.itemView.context.startActivity(intent)
        }

        // Long press listener for delete functionality
        holder.cd.setOnLongClickListener {
            showDeleteDialog(position)
            true
        }
    }

    // Method to show delete dialog
    private fun showDeleteDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Contact")
        builder.setMessage("Are you sure you want to delete this contact?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Remove item from the list
            userList.removeAt(position)
            notifyItemRemoved(position)

            // Save updated list to SharedPreferences
            saveUserList()

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Method to save user list to SharedPreferences
    private fun saveUserList() {
        val gson = Gson()
        val json = gson.toJson(userList)
        val editor = sharedPreferences.edit()
        editor.putString("user_list", json)
        editor.apply()
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
