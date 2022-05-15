package com.picassos.noted.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "todo_lists")
public class TodosList implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int todo_list_id;

    @ColumnInfo(name = "todo_list_identifier")
    private int todo_list_identifier;

    @ColumnInfo(name = "todo_list_title")
    private String todo_list_title;

    public int getTodo_list_id() {
        return todo_list_id;
    }

    public void setTodo_list_id(int todo_list_id) {
        this.todo_list_id = todo_list_id;
    }

    public int getTodo_list_identifier() {
        return todo_list_identifier;
    }

    public void setTodo_list_identifier(int todo_list_identifier) {
        this.todo_list_identifier = todo_list_identifier;
    }

    public String getTodo_list_title() {
        return todo_list_title;
    }

    public void setTodo_list_title(String todo_list_title) {
        this.todo_list_title = todo_list_title;
    }

    @NonNull
    @Override
    public String toString() {
        return todo_list_title;
    }
}
