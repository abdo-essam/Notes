package com.example.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.notes.database.Note

class NotesViewModel ( app: Application) : AndroidViewModel(app) {
    private val repository: NotesRepository = NotesRepository(app)

    suspend fun insertNote(note: Note):Long?{
        return repository.insert(note)
    }

    suspend fun deleteNote(note: Note){
        repository.delete(note)
    }

    suspend fun updateNote(note: Note){
        repository.update(note)
    }

    fun getNotes(email: String): LiveData<List<Note>> {
        return repository.getNotes(email)
    }
}