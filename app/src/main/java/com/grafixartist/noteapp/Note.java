package com.grafixartist.noteapp;

import com.orm.SugarRecord;

/**
 * Created by Suleiman19 on 1/21/16.
 */
public class Note extends SugarRecord {
    String title, note;
    long time;

    public Note() {
    }


    public Note(String title, String note, long time) {
        this.title = title;
        this.note = note;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
