package com.maihuythong.a1712800_simplenote;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDB extends RoomDatabase {
    public abstract NotesDao notesDao();

    public static final String DATABSE_NAME = "notesDb";
    private static NotesDB instance;

    public static NotesDB getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context, NotesDB.class, DATABSE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

}
