package com.example.kidsmarblejar.presentation.marbleJar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kidsmarblejar.databinding.MarbleBinding

class MarbleJarAdapter: RecyclerView.Adapter<MarbleJarViewHolder>() {

    private val items = mutableListOf<Marble>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarbleJarViewHolder {
        val binding = MarbleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarbleJarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarbleJarViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addUser(marble: Marble) {
        items.add(marble)
        notifyDataSetChanged()
    }

    fun removeUser(marble: Marble) {
        items.remove(marble)
        notifyDataSetChanged()
    }

    fun updateAllItems(marbles: List<Marble>) {
        items.clear()
        items.addAll(marbles)
        notifyDataSetChanged()
    }
}