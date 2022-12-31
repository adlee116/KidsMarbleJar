package com.example.kidsmarblejar.presentation.marbleJar

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kidsmarblejar.databinding.MarbleBinding

class MarbleJarViewHolder(private val binding: MarbleBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(marble: Marble) {
        binding.emptyMarbleView.isVisible = !marble.solid
        binding.fullMarbleView.isVisible = marble.solid
    }
}