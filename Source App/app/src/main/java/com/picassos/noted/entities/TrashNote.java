package com.picassos.noted.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "trash_notes")
public class TrashNote implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int note_id;

    @ColumnInfo(name = "note_title")
    private String note_title;

    @ColumnInfo(name = "note_created_at")
    private String note_created_at;

    @ColumnInfo(name = "note_subtitle")
    private String note_subtitle;

    @ColumnInfo(name = "note_description")
    private String note_description;

    @ColumnInfo(name = "note_image_path")
    private String note_image_path;

    @ColumnInfo(name = "note_color")
    private String note_color;

    @ColumnInfo(name = "note_web_link")
    private String note_web_link;

    @ColumnInfo(name = "note_category_id")
    private int note_category_id;

    @ColumnInfo(name = "note_reminder")
    private String note_reminder;

    @ColumnInfo(name = "note_locked")
    private boolean note_locked = false;

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_created_at() {
        return note_created_at;
    }

    public void setNote_created_at(String note_created_at) {
        this.note_created_at = note_created_at;
    }

    public String getNote_subtitle() {
        return note_subtitle;
    }

    public void setNote_subtitle(String note_subtitle) {
        this.note_subtitle = note_subtitle;
    }

    public String getNote_description() {
        return note_description;
    }

    public void setNote_description(String note_description) {
        this.note_description = note_description;
    }

    public String getNote_image_path() {
        return note_image_path;
    }

    public void setNote_image_path(String note_image_path) {
        this.note_image_path = note_image_path;
    }

    public String getNote_color() {
        return note_color;
    }

    public void setNote_color(String note_color) {
        this.note_color = note_color;
    }

    public String getNote_web_link() {
        return note_web_link;
    }

    public void setNote_web_link(String note_web_link) {
        this.note_web_link = note_web_link;
    }

    public int getNote_category_id() {
        return note_category_id;
    }

    public void setNote_category_id(int note_category_id) {
        this.note_category_id = note_category_id;
    }

    public String getNote_reminder() {
        return note_reminder;
    }

    public void setNote_reminder(String note_reminder) {
        this.note_reminder = note_reminder;
    }

    public boolean isNote_locked() {
        return note_locked;
    }

    public void setNote_locked(boolean note_locked) {
        this.note_locked = note_locked;
    }

    @NonNull
    @Override
    public String toString() {
        return note_title + " : " + note_created_at;
    }
}
