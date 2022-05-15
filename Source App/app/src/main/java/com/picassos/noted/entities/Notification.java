package com.picassos.noted.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.picassos.noted.models.NotificationModel;

import java.io.Serializable;

@Entity(tableName = "notification")
public class Notification implements Serializable {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "obj_id")
    public Long obj_id;

    // extra attribute
    @ColumnInfo(name = "read")
    public Boolean read = false;

    @ColumnInfo(name = "created_at")
    public Long created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public static Notification entity(NotificationModel notification) {
        Notification entity = new Notification();
        entity.setId(notification.id);
        entity.setTitle(notification.title);
        entity.setContent(notification.content);
        entity.setRead(notification.read);
        entity.setCreated_at(notification.created_at);
        return entity;
    }
}
