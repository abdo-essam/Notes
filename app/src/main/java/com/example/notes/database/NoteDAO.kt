package com.example.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NoteDAO {
    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes_table WHERE email = :user_email ORDER BY id DESC")
    fun getNotes(user_email: String): LiveData<List<Note>>
}