package com.ishanknijhawan.internshala_notes_app.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ishanknijhawan.internshala_notes_app.dao.NoteDao
import com.ishanknijhawan.internshala_notes_app.db.NoteDatabase
import com.ishanknijhawan.internshala_notes_app.entity.Note
import java.util.*

class NoteRepository(app: Application) {
    var noteDao: NoteDao? = NoteDatabase.getDatabase(app)?.noteDao()

    //function to insert note in database
    fun insert(note: Note): Long? {
        return InsertAsync(
            noteDao
        ).execute(note).get()
    }

    //function to delete note in database
    fun delete(note: Note) {
        DeleteAsync(noteDao).execute(note)
    }

    //function to update note in database
    fun update(note: Note) {
        UpdateAsync(noteDao).execute(note)
    }

    //function to get all notes in database
    fun getAllNotes(uEmail: String): LiveData<List<Note>> {
        return GetAllNotesAsync(
            noteDao
        ).execute(uEmail).get()
    }

    class InsertAsync(var noteDao: NoteDao?) : AsyncTask<Note, Void, Long?>() {
        override fun doInBackground(vararg params: Note): Long? {
            return noteDao?.insert(params[0])
        }
    }

    //background operation to delete note
    class DeleteAsync(var noteDao: NoteDao?) : AsyncTask<Note, Void, Unit>() {
        override fun doInBackground(vararg params: Note) {
            noteDao?.delete(params[0])
        }
    }

    //background operation to  note
    class UpdateAsync(private var noteDao: NoteDao?) : AsyncTask<Note, Void, Unit>() {
        override fun doInBackground(vararg params: Note) {
            noteDao?.update(params[0])
        }
    }

    //background operation to get all notes
    class GetAllNotesAsync(private var noteDao: NoteDao?) : AsyncTask<String, Void, LiveData<List<Note>>>() {
        override fun doInBackground(vararg params: String): LiveData<List<Note>>? {
            return noteDao?.getAllNotes(params[0])
        }
    }
}