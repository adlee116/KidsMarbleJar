package com.example.kidsmarblejar.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kidsmarblejar.databinding.UserAvatarBinding

class UserAdapter: RecyclerView.Adapter<UserViewHolder>() {

    private val items = mutableListOf<UserModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addUser(user: UserModel) {
        items.add(user)
        notifyDataSetChanged()
    }

    fun removeUser(user: UserModel) {
        items.remove(user)
        notifyDataSetChanged()
    }

    fun updateAllItems(users: List<UserModel>) {
        items.clear()
        items.addAll(users)
        notifyDataSetChanged()
    }
}