package com.ishanknijhawan.internshala_notes_app.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ishanknijhawan.internshala_notes_app.entity.Note

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM note_table WHERE userEmail = :uEmail ORDER BY id DESC")
    fun getAllNotes(uEmail: String): LiveData<List<Note>>
}