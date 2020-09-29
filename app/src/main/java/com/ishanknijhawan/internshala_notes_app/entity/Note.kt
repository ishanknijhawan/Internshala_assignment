package com.ishanknijhawan.internshala_notes_app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long?=null,
    var title: String,
    var description: String,
    var userEmail: String
): Serializable