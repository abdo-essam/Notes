package com.example.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long? = null,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "email") var email: String?,
    @ColumnInfo(name = "note_content") var note_content: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "card_color") var card_color: Int,
    val selected : Boolean?=false
) : Serializable
