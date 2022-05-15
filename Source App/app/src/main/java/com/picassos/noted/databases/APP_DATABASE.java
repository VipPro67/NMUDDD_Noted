package com.picassos.noted.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.picassos.noted.entities.ArchiveNote;
import com.picassos.noted.entities.Category;
import com.picassos.noted.entities.Note;
import com.picassos.noted.entities.Notification;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.entities.TodosList;
import com.picassos.noted.entities.TrashNote;

@Database(
        entities = {
                Note.class,
                ArchiveNote.class,
                Category.class,
                Notification.class,
                TrashNote.class,
                Todo.class,
                TodosList.class
        },
        version = 2,
        exportSchema = false)
public abstract class APP_DATABASE extends RoomDatabase {

    public abstract DAO dao();

    private static APP_DATABASE appDatabase;

    public static synchronized APP_DATABASE requestDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, APP_DATABASE.class, "noted_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public static void destroyDatabase() {
        appDatabase = null;
    }
}
