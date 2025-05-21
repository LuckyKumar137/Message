package com.example.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class MessageAdapter(
    private val context: Context,
    private val messageList: MutableList<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageText.text = message.text

        val constraintLayoutParams =
            holder.messageText.layoutParams as ConstraintLayout.LayoutParams

        if (message.senderId == currentUserId) {
            constraintLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            constraintLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            holder.messageText.setBackgroundResource(R.drawable.sent_message_background)
        } else {
            constraintLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            constraintLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            holder.messageText.setBackgroundResource(R.drawable.received_message_background)
        }

        holder.messageText.layoutParams = constraintLayoutParams

        holder.messageText.setOnLongClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes") { dialogInterface, _ ->
                        messageList.removeAt(pos)
                        notifyItemRemoved(pos)
                        notifyItemRangeChanged(pos, messageList.size)
                        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
                        dialogInterface.dismiss()

                        // Save updated message list after delete
                        if (context is chat) {
                            context.saveMessages()
                        }
                    }
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                dialog.show()
            }
            true
        }
    }
}
