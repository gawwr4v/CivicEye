package com.example.reportapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reportapp.databinding.ItemOnboardingBinding
import com.example.reportapp.model.OnboardingItem

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.ivOnboardingImage.setImageResource(item.imageRes)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDescription.text = item.description
    }

    override fun getItemCount(): Int = items.size
}
