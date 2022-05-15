package com.picassos.noted.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.picassos.noted.entities.ArchiveNote;
import com.picassos.noted.entities.Category;
import com.picassos.noted.entities.Note;
import com.picassos.noted.entities.Notification;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.entities.TodosList;
import com.picassos.noted.entities.TrashNote;

import java.util.List;

@Dao
public interface DAO {

    /*
        Note DAO
     */

    // request search notes
    @Query("SELECT * FROM notes ORDER BY CASE WHEN :sort_by = 'note_id' THEN note_id END DESC," +
            "CASE WHEN :sort_by = 'a_z' THEN note_title END ASC," +
            "CASE WHEN :sort_by = 'z_a' THEN note_title END DESC")
    List<Note> request_notes(String sort_by);

    // request search notes by global
    @Query("SELECT * FROM notes WHERE note_title LIKE '%' || :keyword || '%'" +
            "OR note_description LIKE '%' || :keyword || '%' ORDER BY note_id DESC")
    List<Note> request_search_notes_by_global(String keyword);

    // request search notes by color and note theme
    @Query("SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
            "OR note_description LIKE '%' || :keyword || '%') AND note_color = :color ORDER BY note_id DESC")
    List<Note> request_search_notes_by_color(String keyword, String color);

    // request search notes by images
    @Query("SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
            "OR note_description LIKE '%' || :keyword || '%') AND note_image_path != '' ORDER BY note_id DESC")
    List<Note> request_search_notes_by_images(String keyword);

    // request search notes by videos
    @Query("SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
            "OR note_description LIKE '%' || :keyword || '%') AND note_video_path != '' ORDER BY note_id DESC")
    List<Note> request_search_notes_by_video(String keyword);

    // request search notes by reminders
    @Query("SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
            "OR note_description LIKE '%' || :keyword || '%') AND note_reminder != '' ORDER BY note_id DESC")
    List<Note> request_search_notes_by_reminder(String keyword);

    // request search notes by category
    @Query("SELECT * FROM notes WHERE note_category_id = :identifier ORDER BY note_id DESC")
    List<Note> request_notes_by_category(int identifier);

    // request reminder notes
    @Query("SELECT * FROM notes WHERE note_reminder != '' ORDER BY note_id DESC")
    List<Note> request_reminder_notes();

    // request remove lock
    @Query("UPDATE notes SET note_locked = 0")
    int request_remove_lock();

    // request insert a new note
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_note(Note note);

    @Delete
    void request_delete_note(Note note);

    /*
        Archive DAO
     */

    @Query("SELECT * FROM archive_notes ORDER BY note_id DESC")
    List<ArchiveNote> request_archive_notes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_archive_note(ArchiveNote archive_note);

    @Delete
    void request_delete_archive_note(ArchiveNote archive_note);

    /*
        Category DAO
     */

    @Query("SELECT * FROM categories ORDER BY category_id DESC")
    List<Category> request_categories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_category(Category category);

    @Delete
    void request_delete_category(Category category);

    /*
        Notification DAO
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void requestInsertNotification(Notification notification);

    @Query("DELETE FROM notification WHERE id = :id")
    void requestDeleteNotification(long id);

    @Query("DELETE FROM notification")
    void requestDeleteAllNotification();

    @Query("SELECT * FROM notification ORDER BY created_at DESC")
    List<Notification> requestAllNotifications();

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    Notification requestNotification(long id);

    @Query("SELECT COUNT(id) FROM notification WHERE read = 0")
    Integer requestNotificationUnreadCount();

    @Query("SELECT COUNT(id) FROM notification")
    Integer requestNotificationCount();

    /*
        Trash DAO
     */

    @Query("SELECT * FROM trash_notes ORDER BY note_id DESC")
    List<TrashNote> request_trash_notes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_trash_note(TrashNote trash_note);

    @Delete
    void request_delete_trash_note(TrashNote trash_note);

    @Query("DELETE FROM trash_notes")
    void request_delete_all_trash_note();

    /*
        To-Do DAO
     */

    @Query("SELECT * FROM todos ORDER BY CASE WHEN :sort_by = 'todo_id' THEN todo_id END DESC," +
            "CASE WHEN :sort_by = 'a_z' THEN todo_title END ASC," +
            "CASE WHEN :sort_by = 'z_a' THEN todo_title END DESC")
    List<Todo> request_todos(String sort_by);

    @Query("SELECT * FROM todos WHERE todo_state = 0 ORDER BY todo_id DESC")
    List<Todo> request_uncompleted_todos();

    @Query("SELECT * FROM todos WHERE todo_state = 1 ORDER BY todo_id DESC")
    List<Todo> request_completed_todos();

    @Query("SELECT * FROM todos WHERE todo_list = :list ORDER BY todo_id DESC")
    List<Todo> request_todos_by_list(int list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_todo(Todo todo);

    @Delete
    void request_delete_todo(Todo todo);

    @Query("UPDATE todos SET todo_state = :state WHERE todo_id = :id")
    void request_mark_todo(int id, int state);

    @Query("DELETE FROM todos WHERE todo_state = 1")
    void request_delete_completed_todos();

    @Query("SELECT COUNT(todo_id) FROM todos WHERE todo_state = 1")
    Integer request_completed_todos_count();

    /*
        To-Do Lists DAO
     */
    @Query("SELECT * FROM todo_lists ORDER BY todo_list_id DESC")
    List<TodosList> request_todos_lists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void request_insert_todo_list(TodosList todosList);
}