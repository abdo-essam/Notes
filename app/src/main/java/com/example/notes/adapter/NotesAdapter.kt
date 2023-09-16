package com.example.notes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.database.Note
import com.example.notes.databinding.CustomSingleNoteBinding

class NotesAdapter(
    var notes: List<Note>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomSingleNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteTextView.text = notes[position].title
        holder.noteContentTextView.text = notes[position].note_content
        holder.noteDate.text = notes[position].date
        holder.noteCard.setCardBackgroundColor(notes[position].card_color)
        holder.setOnLongClickListener(itemClickListener, position)
    }


    class ViewHolder(itemBinding: CustomSingleNoteBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val noteTextView: TextView = itemBinding.noteTitleText
        val noteContentTextView: TextView = itemBinding.noteContentText
        val noteDate: TextView = itemBinding.dateText
        val noteCard: CardView = itemBinding.singleCard
        fun setOnLongClickListener(clickListener: OnItemClickListener, position: Int) {
            noteCard.setOnClickListener {
                clickListener.onItemClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }


}