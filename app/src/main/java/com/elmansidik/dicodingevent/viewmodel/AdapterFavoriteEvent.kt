package com.elmansidik.dicodingevent.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.databinding.CardItemVerticalBinding

class AdapterFavoriteEvent(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<FavoriteEvent, AdapterFavoriteEvent.FavoriteEventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding =
            CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoriteEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEvent) {
            binding.titleEvent.text = event.name
            binding.descriptionEvent.text = event.description
            Glide.with(binding.imageEvent.context)
                .load(event.imageUrl)
                .into(binding.imageEvent)

            binding.root.setOnClickListener {
                onItemClick?.invoke(event.eventId)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteEvent> =
            object : DiffUtil.ItemCallback<FavoriteEvent>() {
                override fun areItemsTheSame(
                    oldItem: FavoriteEvent,
                    newItem: FavoriteEvent
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: FavoriteEvent,
                    newItem: FavoriteEvent
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

