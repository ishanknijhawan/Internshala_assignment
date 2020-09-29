package com.ishanknijhawan.internshala_notes_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ishanknijhawan.internshala_notes_app.R
import com.ishanknijhawan.internshala_notes_app.entity.Note
import kotlinx.android.synthetic.main.note_item.view.*

class NoteAdapter(
    var items: List<Note>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.note_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        if (items[position].title.isEmpty()) {
//            holder.tvTitleView.visibility = View.GONE
//        }
//        if (items[position].description.isEmpty()) {
//            holder.tvNoteView.visibility = View.GONE
//        }
        holder.tvTitleView.text = items[position].title
        holder.tvNoteView.text = items[position].description
        holder.setOnClick(itemClickListener, position)
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitleView: TextView = itemView.tv_note_title
    val tvNoteView: TextView = itemView.tv_note_text
    val cardView: MaterialCardView = itemView.card_view_note

    fun setOnClick(clickListener: OnItemClickListener, position: Int) {

        cardView.setOnClickListener {
            clickListener.onItemClicked(position)
        }
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int)
}