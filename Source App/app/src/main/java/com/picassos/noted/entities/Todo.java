package com.picassos.noted.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "todos")
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int todo_id;

    @ColumnInfo(name = "todo_title")
    private String todo_title;

    @ColumnInfo(name = "todo_details")
    private String todo_details;

    @ColumnInfo(name = "todo_created_at")
    private String todo_created_at;

    @ColumnInfo(name = "todo_state")
    private boolean todo_state;

    @ColumnInfo(name = "todo_priority")
    private boolean todo_priority;

    @ColumnInfo(name = "todo_list")
    private int todo_list;

    public int getTodo_id() {
        return todo_id;
    }

    public void setTodo_id(int todo_id) {
        this.todo_id = todo_id;
    }

    public String getTodo_title() {
        return todo_title;
    }

    public void setTodo_title(String todo_title) {
        this.todo_title = todo_title;
    }

    public String getTodo_created_at() {
        return todo_created_at;
    }

    public void setTodo_created_at(String todo_created_at) {
        this.todo_created_at = todo_created_at;
    }

    public boolean isTodo_state() {
        return todo_state;
    }

    public void setTodo_state(boolean todo_state) {
        this.todo_state = todo_state;
    }

    public boolean isTodo_priority() {
        return todo_priority;
    }

    public void setTodo_priority(boolean todo_priority) {
        this.todo_priority = todo_priority;
    }

    public String getTodo_details() {
        return todo_details;
    }

    public void setTodo_details(String todo_details) {
        this.todo_details = todo_details;
    }

    public int getTodo_list() {
        return todo_list;
    }

    public void setTodo_list(int todo_list) {
        this.todo_list = todo_list;
    }

    @NonNull
    @Override
    public String toString() {
        return todo_title + " : " + todo_created_at;
    }
}
