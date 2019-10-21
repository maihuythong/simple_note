package com.maihuythong.a1712800_simplenote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String noteTitle;
    @ColumnInfo (name = "text")
    private String noteContent;
    @ColumnInfo (name = "date")
    private long noteDate;
    @ColumnInfo (name = "tag")
    private String noteTag;

    private boolean checked = false;


    public Note(String noteTitle, String noteContent, long noteDate, String noteTag) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteDate = noteDate;
        this.noteTag = noteTag;
    }



    public int getId() {
        return id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public void setNoteTag(String noteTag) {
        this.noteTag = noteTag;
    }

    public String getNoteTag() {
        return noteTag;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
