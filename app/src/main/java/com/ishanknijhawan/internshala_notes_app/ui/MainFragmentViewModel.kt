package com.ishanknijhawan.internshala_notes_app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ishanknijhawan.internshala_notes_app.entity.Note
import com.ishanknijhawan.internshala_notes_app.repository.NoteRepository

class MainFragmentViewModel(app: Application): AndroidViewModel(app) {
    private val repository: NoteRepository = NoteRepository(app)

    fun insertNote(note: Note):Long?{
        return repository.insert(note)
    }

    fun deleteNote(note: Note){
        repository.delete(note)
    }

    fun updateNote(note: Note){
        repository.update(note)
    }

    fun getAllNotes(uEmail: String): LiveData<List<Note>> {
        return repository.getAllNotes(uEmail)
    }
}