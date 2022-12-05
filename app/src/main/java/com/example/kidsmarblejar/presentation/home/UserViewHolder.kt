package com.example.kidsmarblejar.presentation.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kidsmarblejar.R
import com.example.kidsmarblejar.databinding.UserAvatarBinding

class UserViewHolder(private val binding: UserAvatarBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(user: UserModel) {
        binding.userName.text = user.name
        Glide.with(binding.root.context)
            .load(user.image)
            .placeholder(R.drawable.add_user_image)
            .centerCrop()
            .fitCenter()
            .into(binding.userImage)
        setClickListener(user.onClick)
    }

    private fun setClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener { onClick() }
    }
}