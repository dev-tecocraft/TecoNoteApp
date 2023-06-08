package com.teco.note.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.teco.note.databinding.ItemListNoteBinding
import com.teco.note.model.NoteModel
import com.teco.note.util.toDateFormat
import java.lang.Exception

class NoteListAdapter(private val onNoteClick: (NoteModel, ItemListNoteBinding) -> Unit) : Adapter<ViewHolder>() {
    private var notes = ArrayList<NoteModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        NoteListViewHolder(
            ItemListNoteBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as NoteListViewHolder).bind(notes[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNotes(notes: ArrayList<NoteModel>) {
        this.notes.apply {
            clear()
            addAll(notes)
        }
        this.notes = ArrayList(notes.sortedBy { it.date })
        notifyDataSetChanged()
    }

    fun addNote(noteModel: NoteModel) {
        notes.add(noteModel)
        this.notes = ArrayList(notes.sortedBy { it.date })
        notifyItemInserted(notes.size)
    }

    fun updateNote(note: NoteModel) {
        try{
            val noteIndex = notes.indexOf(notes.single { it.noteId == note.noteId })
            this.notes.apply {
                removeAt(noteIndex)
                add(noteIndex, note)
                notifyItemChanged(noteIndex)
            }
        }catch (_: Exception){}
    }

    fun removeNote(note: NoteModel) {
        try{
            val noteIndex = notes.indexOf(notes.single { it.noteId == note.noteId })
            this.notes.apply {
                removeAt(noteIndex)
                notifyItemRemoved(noteIndex)
            }
        }catch (_: Exception){}
    }

    inner class NoteListViewHolder(
        private val binding: ItemListNoteBinding
    ) : ViewHolder(binding.root) {
        fun bind(noteModel: NoteModel) {
            with(binding){
                tvTitle.text = noteModel.title
                tvDesc.text = noteModel.noteDesc
                tvDate.text = noteModel.date.toDateFormat()
                root.setOnClickListener {
                    onNoteClick(noteModel,binding)
                }
            }
        }

    }
}
