package com.example.library

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_MESSAGE = 1
    private val TYPE_TYPING = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isTyping) TYPE_TYPING else TYPE_MESSAGE
    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.messageContainer)
        val avatar: ImageView = view.findViewById(R.id.imgAvatar)
        val sender: TextView = view.findViewById(R.id.tvSender)
        val bubble: TextView = view.findViewById(R.id.tvMessage)
    }

    inner class TypingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_TYPING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_typing, parent, false)
            TypingViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val msg = messages[position]

        if (holder is MessageViewHolder) {

            holder.sender.text = if (msg.isUser) "Você" else "Assistente"
            holder.bubble.text = msg.text

            // AVATAR + ALINHAMENTO
            if (msg.isUser) {
                // Usuário → direita + bolha azul
                holder.avatar.setImageResource(android.R.drawable.sym_def_app_icon)
                holder.container.gravity = Gravity.END
                holder.bubble.setBackgroundResource(R.drawable.bubble_user)

            } else {
                // Bot → esquerda + bolha neutra
                holder.avatar.setImageResource(android.R.drawable.sym_def_app_icon)
                holder.container.gravity = Gravity.START
                holder.bubble.setBackgroundResource(R.drawable.bubble_bot)
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun addTyping() {
        messages.add(ChatMessage("", false, isTyping = true))
        notifyItemInserted(messages.size - 1)
    }

    fun removeTyping() {
        val index = messages.indexOfLast { it.isTyping }
        if (index != -1) {
            messages.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
