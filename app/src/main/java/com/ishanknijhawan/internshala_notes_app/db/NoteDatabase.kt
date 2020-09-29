package com.ishanknijhawan.internshala_notes_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ishanknijhawan.internshala_notes_app.dao.NoteDao
import com.ishanknijhawan.internshala_notes_app.entity.Note

@Database(entities = [Note::class],version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        var instance: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, NoteDatabase::class.java,
                    "note_database"
                ).build()
            }
            return instance
        }
    }
}