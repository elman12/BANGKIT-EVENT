package com.elmansidik.dicodingevent.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.CardItemVerticalBinding
import com.elmansidik.dicodingevent.utils.ListEventsDiffUtil

class AdapterVerticalEvent(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<ListEventsItem, AdapterVerticalEvent.VerticalEventViewHolder>(ListEventsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalEventViewHolder {
        val binding =
            CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VerticalEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VerticalEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.titleEvent.text = event.name
            binding.descriptionEvent.text = event.summary
            Glide.with(binding.imageEvent.context)
                .load(event.imageLogo)
                .into(binding.imageEvent)

            binding.root.setOnClickListener {
                onItemClick?.invoke(event.id)
            }
        }
    }
}