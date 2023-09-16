package com.example.notes

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.notes.database.Note
import com.example.notes.database.NoteDAO
import com.example.notes.database.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepository(app:Application) {
    var noteDAO: NoteDAO? = NoteDatabase.getDatabase(app)?.noteDao()


    suspend fun insert(note: Note): Long? {
        return withContext(Dispatchers.IO) {
            noteDAO?.insert(note)
        }
    }

    suspend fun delete(note: Note) {
        withContext(Dispatchers.IO) {
            noteDAO?.delete(note)
        }
    }

    suspend fun update(note: Note) {
        withContext(Dispatchers.IO) {
            noteDAO?.update(note)
        }
    }


    fun getNotes(uEmail: String): LiveData<List<Note>> {
        return noteDAO?.getNotes(uEmail) ?: error("NoteDAO is null")
    }

}