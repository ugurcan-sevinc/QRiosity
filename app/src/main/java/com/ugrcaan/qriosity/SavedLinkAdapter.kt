package com.ugrcaan.qriosity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ugrcaan.qriosity.model.SavedLink

class SavedLinkAdapter(
    private val listener: SavedLinkListener
) : ListAdapter<SavedLink, SavedLinkAdapter.SavedLinkViewHolder>(DiffCallback) {

    interface SavedLinkListener {
        fun onOpenLink(savedLink: SavedLink)
        fun onDeleteLink(savedLink: SavedLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedLinkViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_saved_link_item, parent, false)
        return SavedLinkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedLinkViewHolder, position: Int) {
        val savedLink = getItem(position)
        holder.textViewName.text = savedLink.name
        holder.textViewLink.text = savedLink.link

        holder.openLinkButton.setOnClickListener {
            listener.onOpenLink(savedLink)
        }

        holder.itemView.setOnLongClickListener {
            listener.onDeleteLink(savedLink)
            true
        }
    }

    class SavedLinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewLink: TextView = itemView.findViewById(R.id.textViewLink)
        val openLinkButton: ImageButton = itemView.findViewById(R.id.open_link_in_webview_button)
    }

    private object DiffCallback : DiffUtil.ItemCallback<SavedLink>() {
        override fun areItemsTheSame(oldItem: SavedLink, newItem: SavedLink): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SavedLink, newItem: SavedLink): Boolean =
            oldItem == newItem
    }
}
