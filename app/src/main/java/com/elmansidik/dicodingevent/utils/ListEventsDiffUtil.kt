package com.elmansidik.dicodingevent.utils

import androidx.recyclerview.widget.DiffUtil
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem

class ListEventsDiffUtil : DiffUtil.ItemCallback<ListEventsItem>() {
    override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
        return oldItem == newItem
    }
}
