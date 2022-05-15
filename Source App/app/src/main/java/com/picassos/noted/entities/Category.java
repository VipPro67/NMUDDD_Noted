package com.picassos.noted.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "categories")
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int category_id;

    @ColumnInfo(name = "category_title")
    private String category_title;

    @ColumnInfo(name = "category_is_primary")
    private boolean category_is_primary;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public boolean isCategory_is_primary() {
        return category_is_primary;
    }

    public void setCategory_is_primary(boolean category_is_primary) {
        this.category_is_primary = category_is_primary;
    }

    @NonNull
    @Override
    public String toString() {
        return category_id + " : " + category_title;
    }
}
