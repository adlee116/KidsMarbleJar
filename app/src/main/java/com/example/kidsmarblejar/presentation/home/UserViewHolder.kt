package com.example.kidsmarblejar.presentation.home

import android.net.Uri
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kidsmarblejar.R
import com.example.kidsmarblejar.databinding.UserAvatarBinding

class UserViewHolder(private val binding: UserAvatarBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(user: UserModel) {
        binding.userName.text = user.name
        val uri = getImageUriFromString(user.image)
        Glide.with(binding.root.context)
            .load(uri)
            .placeholder(R.drawable.add_user_image)
            .centerCrop()
            .fitCenter()
            .into(binding.userImage)
        setClickListener(user.onClick)
    }

    private fun getImageUriFromString(uriString: String): Uri {
        return uriString.toUri()
    }

    private fun setClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener { onClick() }
    }
}